package com.trademaster.ims.mobile.help;

import com.trademaster.ims.mobile.auth.service.CustomerAuthException;
import com.trademaster.ims.mobile.help.model.HelpFaq;
import com.trademaster.ims.mobile.help.repository.HelpFaqRepository;
import com.trademaster.ims.mobile.help.service.HelpFaqService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HelpFaqServiceTest {
    @Mock HelpFaqRepository faqs;
    HelpFaqService service;

    @BeforeEach void setUp() {
        service = new HelpFaqService(faqs);
        when(faqs.existsByFaqCode(anyString())).thenReturn(true);
    }

    @Test void listReturnsActiveRepositoryRowsAndAppliesSearch() {
        when(faqs.findByActiveTrueOrderByCategoryAscDisplayOrderAscQuestionAsc()).thenReturn(List.of(
                faq("ORDERS_TRACKING", "Orders", "How do I track my order?", "Use My Orders."),
                faq("PAYMENT_HELP", "Payments", "How do I pay?", "Use checkout.")));

        var result = service.list(null, "track");

        assertEquals(1, result.size());
        assertEquals("ORDERS_TRACKING", result.get(0).faqCode());
    }

    @Test void categoryFilterUsesActiveCategoryQuery() {
        when(faqs.findByActiveTrueAndCategoryIgnoreCaseOrderByDisplayOrderAscQuestionAsc("Orders"))
                .thenReturn(List.of(faq("ORDERS_TRACKING", "Orders", "How do I track my order?", "Use My Orders.")));

        var result = service.list("Orders", null);

        assertEquals(1, result.size());
        verify(faqs).findByActiveTrueAndCategoryIgnoreCaseOrderByDisplayOrderAscQuestionAsc("Orders");
    }

    @Test void missingOrInactiveFaqIsNotFound() {
        when(faqs.findByFaqCodeAndActiveTrue("MISSING")).thenReturn(Optional.empty());

        CustomerAuthException ex = assertThrows(CustomerAuthException.class, () -> service.get("MISSING"));

        assertEquals(HttpStatus.NOT_FOUND, ex.status());
        assertEquals("FAQ_NOT_FOUND", ex.code());
    }

    private HelpFaq faq(String code, String category, String question, String answer) {
        HelpFaq faq = new HelpFaq();
        faq.setFaqCode(code);
        faq.setCategory(category);
        faq.setQuestion(question);
        faq.setAnswer(answer);
        faq.setDisplayOrder(10);
        faq.setActive(true);
        return faq;
    }
}