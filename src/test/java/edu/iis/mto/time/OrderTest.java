package edu.iis.mto.time;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@ExtendWith(MockitoExtension.class)
class OrderTest {
  @Mock private Clock clock;
  private Order order;
  private final Instant orderSubmit = Instant.parse("2021-04-12T21:35:23.084569400Z");

  @BeforeEach
  void setUp() throws Exception {
    order = new Order(clock);
  }

  @Test
  void order_should_be_confirmed_with_same_order_date() {
    // Given
    Instant orderConfirm = orderSubmit.plus(0, ChronoUnit.HOURS);
    Mockito.when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    Mockito.when(clock.instant()).thenReturn(orderSubmit).thenReturn(orderConfirm);
    order.submit();
    // When
    order.confirm();
    // Then
    Assertions.assertSame(order.getOrderState(), Order.State.CONFIRMED);
  }

  @Test
  void order_should_be_confirmed_with_time_shift_12_hours() {
    // Given
    Instant orderConfirm = orderSubmit.plus(12, ChronoUnit.HOURS);
    Mockito.when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    Mockito.when(clock.instant()).thenReturn(orderSubmit).thenReturn(orderConfirm);
    order.submit();
    // When
    order.confirm();
    // Then
    Assertions.assertSame(order.getOrderState(), Order.State.CONFIRMED);
  }

  @Test
  void order_should_be_confirmed_one_second_before_VALID_PERIOD_HOURS() {
    // Given
    Instant orderConfirm =
        orderSubmit
            .plus(23, ChronoUnit.HOURS)
            .plus(59, ChronoUnit.MINUTES)
            .plus(59, ChronoUnit.SECONDS);
    Mockito.when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    Mockito.when(clock.instant()).thenReturn(orderSubmit).thenReturn(orderConfirm);
    order.submit();
    // When
    order.confirm();
    // Then
    Assertions.assertSame(order.getOrderState(), Order.State.CONFIRMED);
  }

  @Test
  void order_should_be_confirmed_exact_after_24_hours() {
    // Given
    Instant orderConfirm = orderSubmit.plus(24, ChronoUnit.HOURS);
    Mockito.when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    Mockito.when(clock.instant()).thenReturn(orderSubmit).thenReturn(orderConfirm);
    order.submit();
    // When
    order.confirm();
    // Then
    Assertions.assertSame(order.getOrderState(), Order.State.CONFIRMED);
  }

  @Test
  void order_should_throw_exception() {
    // Given
    Instant orderConfirm = orderSubmit.plus(25, ChronoUnit.HOURS);
    Mockito.when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    Mockito.when(clock.instant()).thenReturn(orderSubmit).thenReturn(orderConfirm);
    order.submit();
    // When Then
    Assertions.assertThrows(OrderExpiredException.class, () -> order.confirm());
    Assertions.assertSame(order.getOrderState(), Order.State.CANCELLED);
  }
}
