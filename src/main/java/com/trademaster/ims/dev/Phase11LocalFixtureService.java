package com.trademaster.ims.dev;

import com.trademaster.ims.mobile.auth.model.CustomerAccount;
import com.trademaster.ims.mobile.auth.repository.CustomerAccountRepository;
import com.trademaster.ims.mobile.checkout.model.CustomerAddress;
import com.trademaster.ims.mobile.checkout.model.CustomerOrder;
import com.trademaster.ims.mobile.checkout.model.CustomerOrderItem;
import com.trademaster.ims.mobile.checkout.model.CustomerOrderStatusHistory;
import com.trademaster.ims.mobile.checkout.repository.CustomerAddressRepository;
import com.trademaster.ims.mobile.checkout.repository.CustomerOrderItemRepository;
import com.trademaster.ims.mobile.checkout.repository.CustomerOrderRepository;
import com.trademaster.ims.mobile.checkout.repository.CustomerOrderStatusHistoryRepository;
import com.trademaster.ims.mobile.fulfillment.model.OrderFulfillment;
import com.trademaster.ims.mobile.fulfillment.repository.DeliveryEventRepository;
import com.trademaster.ims.mobile.fulfillment.repository.FulfillmentStatusHistoryRepository;
import com.trademaster.ims.mobile.fulfillment.repository.OrderFulfillmentRepository;
import com.trademaster.ims.mobile.notifications.repository.CustomerNotificationRepository;
import com.trademaster.ims.mobile.payments.model.CustomerPayment;
import com.trademaster.ims.mobile.payments.repository.CustomerPaymentRepository;
import com.trademaster.ims.model.Customer;
import com.trademaster.ims.model.FinancialAccount;
import com.trademaster.ims.model.Inventory;
import com.trademaster.ims.model.Product;
import com.trademaster.ims.model.Role;
import com.trademaster.ims.model.StockMovement;
import com.trademaster.ims.model.User;
import com.trademaster.ims.model.Warehouse;
import com.trademaster.ims.repository.CustomerRepository;
import com.trademaster.ims.repository.FinancialAccountRepository;
import com.trademaster.ims.repository.InventoryRepository;
import com.trademaster.ims.repository.ProductRepository;
import com.trademaster.ims.repository.RoleRepository;
import com.trademaster.ims.repository.StockMovementRepository;
import com.trademaster.ims.repository.UserRepository;
import com.trademaster.ims.repository.WarehouseRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@ConditionalOnProperty(prefix = "techsmart.dev-fixtures", name = "enabled", havingValue = "true")
public class Phase11LocalFixtureService {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2);
    private final CustomerRepository customers;
    private final CustomerAccountRepository accounts;
    private final CustomerAddressRepository addresses;
    private final RoleRepository roles;
    private final UserRepository users;
    private final ProductRepository products;
    private final WarehouseRepository warehouses;
    private final InventoryRepository inventory;
    private final CustomerOrderRepository orders;
    private final CustomerOrderItemRepository items;
    private final CustomerOrderStatusHistoryRepository orderHistory;
    private final CustomerPaymentRepository payments;
    private final FinancialAccountRepository financialAccounts;
    private final StockMovementRepository stockMovements;
    private final OrderFulfillmentRepository fulfillments;
    private final FulfillmentStatusHistoryRepository fulfillmentHistory;
    private final DeliveryEventRepository deliveryEvents;
    private final CustomerNotificationRepository notifications;
    private final PasswordEncoder encoder;

    public Phase11LocalFixtureService(CustomerRepository customers,
                                      CustomerAccountRepository accounts,
                                      CustomerAddressRepository addresses,
                                      RoleRepository roles,
                                      UserRepository users,
                                      ProductRepository products,
                                      WarehouseRepository warehouses,
                                      InventoryRepository inventory,
                                      CustomerOrderRepository orders,
                                      CustomerOrderItemRepository items,
                                      CustomerOrderStatusHistoryRepository orderHistory,
                                      CustomerPaymentRepository payments,
                                      FinancialAccountRepository financialAccounts,
                                      StockMovementRepository stockMovements,
                                      OrderFulfillmentRepository fulfillments,
                                      FulfillmentStatusHistoryRepository fulfillmentHistory,
                                      DeliveryEventRepository deliveryEvents,
                                      CustomerNotificationRepository notifications,
                                      PasswordEncoder encoder) {
        this.customers = customers;
        this.accounts = accounts;
        this.addresses = addresses;
        this.roles = roles;
        this.users = users;
        this.products = products;
        this.warehouses = warehouses;
        this.inventory = inventory;
        this.orders = orders;
        this.items = items;
        this.orderHistory = orderHistory;
        this.payments = payments;
        this.financialAccounts = financialAccounts;
        this.stockMovements = stockMovements;
        this.fulfillments = fulfillments;
        this.fulfillmentHistory = fulfillmentHistory;
        this.deliveryEvents = deliveryEvents;
        this.notifications = notifications;
        this.encoder = encoder;
    }

    @Transactional
    public FixtureResponse create() {
        String run = String.valueOf(System.currentTimeMillis());
        String customerPassword = password();
        String adminPassword = password();
        CustomerAccount a = account("phase11.customer.a@example.test", "Phase 11 Customer A", "01711000001", customerPassword);
        CustomerAccount b = account("phase11.customer.b@example.test", "Phase 11 Customer B", "01711000002", customerPassword);
        User admin = admin(adminPassword);
        Warehouse warehouse = warehouse();
        ensurePostingAccount(warehouse.getId(), admin.getUserId());
        Product p1 = product("PHASE11_FIXTURE_" + run + "_A", "Phase 11 Test Keyboard " + run, new BigDecimal("500.00"));
        Product p2 = product("PHASE11_FIXTURE_" + run + "_B", "Phase 11 Test Mouse " + run, new BigDecimal("300.00"));
        stock(warehouse.getId(), p1.getId(), 200);
        stock(warehouse.getId(), p2.getId(), 200);
        CustomerOrder prepaid = order(a, "P11P-" + run, CustomerOrder.PaymentStatus.PAID, CustomerOrder.AccountingStatus.POSTED,
                List.of(line(p1, 2), line(p2, 2)));
        payment(prepaid, "P11PAY-P-" + run, CustomerPayment.PaymentStatus.PAID, CustomerPayment.AccountingStatus.POSTED, "LOCAL_TEST_CARD", "CARD", true);
        CustomerOrder cod = order(a, "P11C-" + run, CustomerOrder.PaymentStatus.COD_PENDING, CustomerOrder.AccountingStatus.UNPOSTED,
                List.of(line(p1, 1)));
        payment(cod, "P11PAY-C-" + run, CustomerPayment.PaymentStatus.COD_PENDING, CustomerPayment.AccountingStatus.UNPOSTED, "COD", "COD", false);
        return new FixtureResponse(true,
                new Credential("customerA", a.getEmail(), customerPassword),
                new Credential("customerB", b.getEmail(), customerPassword),
                new Credential("admin", admin.getUsername(), adminPassword),
                prepaid.getOrderNumber(), cod.getOrderNumber(), warehouse.getId(), List.of(p1.getId(), p2.getId()),
                "Disposable Phase 11 local fixture created. Use only in local/dev validation. Do not store returned passwords.");
    }

    @Transactional(readOnly = true)
    public VerificationResponse verify(String orderNumber) {
        CustomerOrder order = orders.findByOrderNumber(orderNumber).orElseThrow();
        OrderFulfillment fulfillment = fulfillments.findByOrderNumber(orderNumber).orElse(null);
        List<CustomerOrderItem> orderItems = items.findByOrderIdOrderByIdAsc(order.getId());
        String prefix = "CUSTOMER_ORDER_FULFILLMENT:" + orderNumber + ":";
        List<StockMovement> movements = stockMovements.findByMovementType(StockMovement.MovementType.CUSTOMER_ORDER_FULFILLMENT)
                .stream()
                .filter(m -> m.getReferenceNo() != null && m.getReferenceNo().startsWith(prefix))
                .toList();
        int movementQuantitySum = movements.stream().mapToInt(m -> m.getQuantity() == null ? 0 : m.getQuantity()).sum();
        List<InventorySnapshot> stock = orderItems.stream()
                .map(i -> inventory.findByProductId(i.getProductId()).stream().findFirst()
                        .map(inv -> new InventorySnapshot(i.getProductId(), inv.getWarehouseId(), inv.getQuantity(), inv.getAvailableQuantity()))
                        .orElse(new InventorySnapshot(i.getProductId(), null, null, null)))
                .toList();
        CustomerPayment payment = payments.findTopByOrderIdOrderByCreatedAtDesc(order.getId()).orElse(null);
        long notificationCount = notifications.findAll().stream()
                .filter(n -> n.getCustomer().getId().equals(order.getAccountId()))
                .filter(n -> contains(n.getEventKey(), orderNumber) || contains(n.getRelatedEntityReference(), orderNumber) || contains(n.getActionReference(), orderNumber))
                .count();
        return new VerificationResponse(order.getOrderNumber(), order.getStatus().name(), order.getPaymentStatus().name(),
                order.getAccountingStatus().name(), fulfillment == null ? null : fulfillment.getStatus().name(),
                fulfillment != null && fulfillment.isStockDeducted(),
                fulfillment == null ? 0 : fulfillmentHistory.findByFulfillmentIdOrderByOccurredAtAsc(fulfillment.getId()).size(),
                fulfillment == null ? 0 : deliveryEvents.findByFulfillmentIdOrderByOccurredAtAsc(fulfillment.getId()).size(),
                movements.size(), movementQuantitySum, notificationCount,
                payment == null ? null : payment.getPaymentNumber(),
                payment == null ? null : payment.getPaymentStatus().name(),
                payment == null ? null : payment.getAccountingStatus().name(),
                payment == null ? null : payment.getPostingKey(),
                payment == null ? null : payment.getLedgerEntryId(), stock);
    }

    @Transactional
    public CleanupResponse cleanup() {
        List<Product> fixtureProducts = products.findAll().stream()
                .filter(this::isFixtureProduct)
                .toList();
        List<Long> productIds = new ArrayList<>();
        List<String> productCodes = new ArrayList<>();
        int newlyHidden = 0;
        for (Product product : fixtureProducts) {
            productIds.add(product.getId());
            productCodes.add(product.getProductCode());
            if (product.getStatus() != Product.ProductStatus.INACTIVE) {
                product.setStatus(Product.ProductStatus.INACTIVE);
                newlyHidden++;
            }
        }
        products.saveAll(fixtureProducts);
        return new CleanupResponse(fixtureProducts.size(), newlyHidden, productIds, productCodes);
    }

    private boolean isFixtureProduct(Product product) {
        return startsWith(product.getProductCode(), "P11-")
                || startsWith(product.getProductCode(), "PHASE11_FIXTURE_")
                || startsWith(product.getSku(), "P11-")
                || startsWith(product.getSku(), "PHASE11_FIXTURE_")
                || startsWith(product.getProductName(), "Phase 11 Test")
                || contains(product.getDescription(), "Phase 11 local fulfillment fixture product")
                || contains(product.getDescription(), "fixtureData=true");
    }

    private boolean startsWith(String value, String prefix) {
        return value != null && value.startsWith(prefix);
    }
    private boolean contains(String value, String needle) {
        return value != null && value.contains(needle);
    }

    private CustomerAccount account(String email, String name, String phone, String rawPassword) {
        Customer customer = customers.findByEmail(email).orElseGet(() -> {
            Customer c = new Customer("P11-" + email.substring(8, 18).replace('.', '-').toUpperCase(), name, email, phone);
            c.setMobile(phone);
            c.setAddress("Phase 11 fixture address");
            c.setCity("Dhaka");
            c.setCountry("Bangladesh");
            return customers.save(c);
        });
        customer.setCustomerName(name);
        customer.setEmail(email);
        customer.setPhone(phone);
        customer.setMobile(phone);
        customer.setStatus(true);
        customer = customers.save(customer);
        CustomerAccount account = accounts.findByEmailIgnoreCase(email).orElseGet(CustomerAccount::new);
        account.setCustomer(customer);
        account.setEmail(email);
        account.setPasswordHash(encoder.encode(rawPassword));
        account.setStatus(CustomerAccount.Status.ACTIVE);
        account.setEmailVerified(true);
        account.setFailedLoginAttempts(0);
        account.setLockedUntil(null);
        account.setPasswordChangedAt(Instant.now());
        account = accounts.save(account);
        if (addresses.countByAccountIdAndActiveTrue(account.getId()) == 0) {
            CustomerAddress address = new CustomerAddress();
            address.setAccountId(account.getId());
            address.setLabel("Phase 11 Fixture Home");
            address.setRecipientName(name);
            address.setPhone(phone);
            address.setAddressLine1("Phase 11 Fixture Road");
            address.setArea("Dhanmondi");
            address.setCity("Dhaka");
            address.setDistrict("Dhaka");
            address.setDivision("Dhaka");
            address.setPostalCode("1209");
            address.setCountry("Bangladesh");
            address.setDefaultAddress(true);
            address.setActive(true);
            addresses.save(address);
        }
        return account;
    }

    private User admin(String rawPassword) {
        Role role = roles.findByRoleName("SUPER_ADMIN").orElseGet(() -> {
            Role r = new Role();
            r.setRoleName("SUPER_ADMIN");
            r.setDescription("Phase 11 local fixture super admin role");
            r.setStatus(true);
            return roles.save(r);
        });
        User user = users.findByUsername("phase11.admin@example.test").orElseGet(User::new);
        user.setUsername("phase11.admin@example.test");
        user.setEmail("phase11.admin@example.test");
        user.setFullName("Phase 11 Fixture Admin");
        user.setPhone("01711000003");
        user.setDepartment("Local QA");
        user.setDesignation("Fixture Operator");
        user.setRoleId(role.getRoleId());
        user.setIsActive(true);
        user.setPasswordHash(encoder.encode(rawPassword));
        return users.save(user);
    }

    private Warehouse warehouse() {
        return warehouses.findByWarehouseCode("P11-LOCAL").orElseGet(() -> {
            Warehouse w = new Warehouse();
            w.setWarehouseCode("P11-LOCAL");
            w.setName("Phase 11 Local Fixture Warehouse");
            w.setLocation("Local validation only");
            w.setStatus("ACTIVE");
            w.setCapacity(10000);
            w.setManagerName("Phase 11 Fixture");
            w.setContactPhone("01711000004");
            return warehouses.save(w);
        });
    }

    private void ensurePostingAccount(Long warehouseId, Long adminId) {
        boolean hasActive = financialAccounts.findAll().stream().anyMatch(a -> a.getStatus() == FinancialAccount.AccountStatus.ACTIVE);
        if (hasActive) return;
        FinancialAccount a = new FinancialAccount();
        a.setAccountCode("P11-CASH");
        a.setAccountName("Phase 11 Local Fixture Cash");
        a.setAccountType(FinancialAccount.AccountType.CASH);
        a.setCurrencyCode("BDT");
        a.setOpeningBalance(BigDecimal.ZERO);
        a.setCurrentBalance(BigDecimal.ZERO);
        a.setStatus(FinancialAccount.AccountStatus.ACTIVE);
        a.setCompanyId(1L);
        a.setWarehouseId(warehouseId);
        a.setCreatedBy(adminId);
        financialAccounts.save(a);
    }

    private Product product(String code, String name, BigDecimal price) {
        Product p = new Product();
        p.setProductCode(code);
        p.setSku(code);
        p.setProductName(name);
        p.setDescription("Disposable Phase 11 local fulfillment fixture product; fixtureData=true; catalogVisible=false");
        p.setBuyingPrice(price.divide(new BigDecimal("2.00")));
        p.setSellingPrice(price);
        p.setTaxRate(BigDecimal.ZERO);
        p.setMinStockLevel(1);
        p.setReorderLevel(1);
        p.setMaxStockLevel(1000);
        p.setSelectUnit("pcs");
        p.setStatus(Product.ProductStatus.INACTIVE);
        return products.save(p);
    }

    private void stock(Long warehouseId, Long productId, int quantity) {
        Inventory inv = inventory.findByProductIdAndWarehouseId(productId, warehouseId).orElseGet(() -> new Inventory(1L, warehouseId, productId, 0));
        inv.setCompanyId(1L);
        inv.setWarehouseId(warehouseId);
        inv.setProductId(productId);
        inv.setQuantity(quantity);
        inv.setReservedQuantity(0);
        inv.updateAvailableQuantity();
        inventory.save(inv);
    }

    private OrderLine line(Product product, int qty) { return new OrderLine(product, qty); }

    private CustomerOrder order(CustomerAccount account, String orderNumber, CustomerOrder.PaymentStatus paymentStatus,
                                CustomerOrder.AccountingStatus accountingStatus, List<OrderLine> lines) {
        BigDecimal subtotal = lines.stream().map(l -> l.product().getSellingPrice().multiply(BigDecimal.valueOf(l.quantity()))).reduce(ZERO, BigDecimal::add);
        BigDecimal delivery = new BigDecimal("60.00");
        CustomerOrder order = new CustomerOrder();
        order.setOrderNumber(orderNumber);
        order.setAccountId(account.getId());
        order.setIdempotencyKey("phase11-fixture-" + orderNumber);
        order.setReviewId(UUID.randomUUID().toString());
        order.setStatus(CustomerOrder.OrderStatus.CONFIRMED);
        order.setPaymentStatus(paymentStatus);
        order.setAccountingStatus(accountingStatus);
        order.setSubtotal(subtotal);
        order.setTax(ZERO);
        order.setDelivery(delivery);
        order.setTotal(subtotal.add(delivery));
        order.setRecipient(account.getCustomer().getCustomerName());
        order.setPhone(account.getCustomer().getPhone());
        order.setAddressSnapshot("Phase 11 Fixture Road, Dhaka, Bangladesh");
        order.setDeliverySnapshot("Standard delivery - Phase 11 fixture");
        order.setNote("Phase 11 disposable local fixture order");
        order.setSubmittedAt(Instant.now());
        order = orders.save(order);
        for (OrderLine line : lines) {
            CustomerOrderItem item = new CustomerOrderItem();
            item.setOrderId(order.getId());
            item.setProductId(line.product().getId());
            item.setProductName(line.product().getProductName());
            item.setProductCode(line.product().getProductCode());
            item.setUnitPrice(line.product().getSellingPrice());
            item.setQuantity(line.quantity());
            item.setTaxRate(BigDecimal.ZERO);
            item.setTaxAmount(ZERO);
            item.setLineSubtotal(line.product().getSellingPrice().multiply(BigDecimal.valueOf(line.quantity())));
            items.save(item);
        }
        CustomerOrderStatusHistory h = new CustomerOrderStatusHistory();
        h.setOrderId(order.getId());
        h.setStatus(CustomerOrder.OrderStatus.CONFIRMED.name());
        h.setCustomerNote("Order confirmed for Phase 11 local validation.");
        orderHistory.save(h);
        return order;
    }

    private void payment(CustomerOrder order, String paymentNumber, CustomerPayment.PaymentStatus paymentStatus,
                         CustomerPayment.AccountingStatus accountingStatus, String methodCode, String methodType, boolean posted) {
        CustomerPayment payment = new CustomerPayment();
        payment.setPaymentNumber(paymentNumber);
        payment.setOrderId(order.getId());
        payment.setOrderNumber(order.getOrderNumber());
        payment.setAccountId(order.getAccountId());
        payment.setMethodCode(methodCode);
        payment.setMethodType(methodType);
        payment.setAmount(order.getTotal());
        payment.setCurrency("BDT");
        payment.setPaymentStatus(paymentStatus);
        payment.setAccountingStatus(accountingStatus);
        payment.setCustomerVisibleMessage(posted ? "Payment received for local validation." : "Cash on Delivery pending.");
        if (posted) {
            payment.setPaidAt(Instant.now());
            payment.setVerifiedAt(Instant.now());
            payment.setPostedAt(Instant.now());
            payment.setPostingKey("CUSTOMER_PAYMENT:" + paymentNumber);
        }
        payments.save(payment);
    }

    private String password() {
        byte[] bytes = new byte[9];
        RANDOM.nextBytes(bytes);
        return "P11!" + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes) + "aA1";
    }

    private record OrderLine(Product product, int quantity) {}
    public record Credential(String label, String username, String password) {}
    public record FixtureResponse(boolean created, Credential customerA, Credential customerB, Credential admin,
                                  String prepaidOrderNumber, String codOrderNumber, Long warehouseId,
                                  List<Long> productIds, String note) {}
    public record CleanupResponse(int matchedProducts, int newlyHiddenProducts, List<Long> productIds, List<String> productCodes) {}
    public record InventorySnapshot(Long productId, Long warehouseId, Integer quantity, Integer availableQuantity) {}
    public record VerificationResponse(String orderNumber, String orderStatus, String paymentStatus, String accountingStatus,
                                       String fulfillmentStatus, boolean stockDeducted, int fulfillmentHistoryCount,
                                       int deliveryEventCount, int stockMovementCount, int stockMovementQuantitySum,
                                       long notificationCount, String paymentNumber, String customerPaymentStatus,
                                       String customerPaymentAccountingStatus, String postingKey, Long ledgerEntryId,
                                       List<InventorySnapshot> inventory) {}
}