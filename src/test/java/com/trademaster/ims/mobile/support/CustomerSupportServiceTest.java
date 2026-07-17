package com.trademaster.ims.mobile.support;

import com.trademaster.ims.mobile.auth.service.CustomerAuthException;
import com.trademaster.ims.mobile.checkout.model.CustomerOrder;
import com.trademaster.ims.mobile.checkout.repository.CustomerOrderRepository;
import com.trademaster.ims.mobile.support.dto.SupportDtos.AddMessageRequest;
import com.trademaster.ims.mobile.support.dto.SupportDtos.CreateTicketRequest;
import com.trademaster.ims.mobile.support.model.SupportMessage;
import com.trademaster.ims.mobile.support.model.SupportTicket;
import com.trademaster.ims.mobile.support.repository.SupportMessageRepository;
import com.trademaster.ims.mobile.support.repository.SupportTicketRepository;
import com.trademaster.ims.mobile.support.service.CustomerSupportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerSupportServiceTest {
    @Mock SupportTicketRepository tickets;
    @Mock SupportMessageRepository messages;
    @Mock CustomerOrderRepository orders;
    CustomerSupportService service;

    @BeforeEach void setUp() {
        service = new CustomerSupportService(tickets, messages, orders);
    }

    @Test void createsTicketForCustomerAndPersistsFirstVisibleMessage() {
        when(tickets.saveAndFlush(any(SupportTicket.class))).thenAnswer(invocation -> {
            SupportTicket ticket = invocation.getArgument(0);
            ReflectionTestUtils.setField(ticket, "id", 55L);
            return ticket;
        });
        when(messages.findByTicketIdAndCustomerVisibleTrueOrderByCreatedAtAsc(55L)).thenReturn(List.of());

        var detail = service.create(7L,
                new CreateTicketRequest("Need help", "ORDER", "HIGH", null, "Please check this."));

        assertEquals("Need help", detail.subject());
        assertEquals("ORDER", detail.category());
        assertEquals("HIGH", detail.priority());
        verify(messages).save(any(SupportMessage.class));
    }

    @Test void rejectsRelatedOrderOwnedByAnotherCustomer() {
        when(orders.findByOrderNumberAndAccountId("ORD-OTHER", 7L)).thenReturn(Optional.empty());

        CustomerAuthException ex = assertThrows(CustomerAuthException.class, () -> service.create(7L,
                new CreateTicketRequest("Need help", "ORDER", null, "ORD-OTHER", "Please check this.")));

        assertEquals(HttpStatus.BAD_REQUEST, ex.status());
        assertEquals("ORDER_NOT_FOUND", ex.code());
        verify(tickets, never()).saveAndFlush(any());
    }

    @Test void detailRequiresTicketOwnershipAndReturnsOnlyVisibleMessages() {
        SupportTicket ticket = ticket(50L, 7L, "SUP-1");
        SupportMessage visible = message(50L, SupportMessage.SenderType.ADMIN, "Visible reply", true);
        when(tickets.findByTicketNumberAndAccountId("SUP-1", 7L)).thenReturn(Optional.of(ticket));
        when(messages.findByTicketIdAndCustomerVisibleTrueOrderByCreatedAtAsc(50L)).thenReturn(List.of(visible));

        var detail = service.detail(7L, "SUP-1");

        assertEquals(1, detail.messages().size());
        assertEquals("Visible reply", detail.messages().get(0).message());
        verify(messages).findByTicketIdAndCustomerVisibleTrueOrderByCreatedAtAsc(50L);
    }

    @Test void customerCannotOpenAnotherCustomersTicket() {
        when(tickets.findByTicketNumberAndAccountId("SUP-2", 7L)).thenReturn(Optional.empty());

        CustomerAuthException ex = assertThrows(CustomerAuthException.class, () -> service.detail(7L, "SUP-2"));

        assertEquals(HttpStatus.NOT_FOUND, ex.status());
        assertEquals("SUPPORT_TICKET_NOT_FOUND", ex.code());
    }

    @Test void closedTicketCannotReceiveCustomerReplies() {
        SupportTicket ticket = ticket(51L, 7L, "SUP-3");
        ticket.setStatus(SupportTicket.Status.CLOSED);
        when(tickets.findByTicketNumberAndAccountId("SUP-3", 7L)).thenReturn(Optional.of(ticket));

        CustomerAuthException ex = assertThrows(CustomerAuthException.class,
                () -> service.reply(7L, "SUP-3", new AddMessageRequest("Still need help")));

        assertEquals(HttpStatus.CONFLICT, ex.status());
        assertEquals("TICKET_CLOSED", ex.code());
    }

    private SupportTicket ticket(Long id, Long accountId, String number) {
        SupportTicket ticket = new SupportTicket();
        ReflectionTestUtils.setField(ticket, "id", id);
        ticket.setAccountId(accountId);
        ticket.setTicketNumber(number);
        ticket.setSubject("Subject");
        ticket.setCategory(SupportTicket.Category.ORDER);
        ticket.setPriority(SupportTicket.Priority.NORMAL);
        return ticket;
    }

    private SupportMessage message(Long ticketId, SupportMessage.SenderType senderType, String text, boolean visible) {
        SupportMessage message = new SupportMessage();
        message.setTicketId(ticketId);
        message.setSenderType(senderType);
        message.setMessage(text);
        message.setCustomerVisible(visible);
        return message;
    }
}