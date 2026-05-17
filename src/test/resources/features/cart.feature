@regression @cart
Feature: GreenKart Shopping Cart
  As a user I want to manage my cart and apply coupon codes on the checkout page

  # The GreenKart flow:
  # 1. Add products → 2. Open cart panel → 3. PROCEED TO CHECKOUT → 4. Apply coupon + Place Order

  @coupon @smoke
  Scenario: Apply a valid coupon code on checkout page
    Given I am on the GreenKart home page
    And I add product "Brocolli" to the cart
    And I proceed to the cart
    And I proceed to checkout
    When I apply coupon code "rahulshettyacademy"
    Then the coupon should be applied successfully
    And the discounted price should be displayed

  @coupon
  Scenario: Apply an invalid coupon code
    Given I am on the GreenKart home page
    And I add product "Brocolli" to the cart
    And I proceed to the cart
    And I proceed to checkout
    When I apply coupon code "INVALID123"
    Then the coupon should not be applied

  @cart-management
  Scenario: Remove a product from cart panel
    Given I am on the GreenKart home page
    And I add product "Brocolli" to the cart
    And I proceed to the cart
    Then the cart should contain "Brocolli"
    When I remove item at position 0 from cart
    Then the cart should be empty

  @coupon @scenario-outline
  Scenario Outline: Apply different coupon codes
    Given I am on the GreenKart home page
    And I add product "Brocolli" to the cart
    And I proceed to the cart
    And I proceed to checkout
    When I apply coupon code "<couponCode>"
    Then the coupon message should contain "<expectedMessage>"

    Examples:
      | couponCode            | expectedMessage |
      | rahulshettyacademy    | %               |
      | INVALIDCODE           | 0               |

  @cart-multi
  Scenario: Add multiple different products to cart and check count
    Given I am on the GreenKart home page
    When I add product "Brocolli" to the cart
    And I add product "Cauliflower" to the cart
    And I add product "Beetroot" to the cart
    Then the cart count should be 3
