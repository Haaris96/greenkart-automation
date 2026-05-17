@regression @checkout @e2e
Feature: GreenKart End-to-End Checkout
  As a user I want to complete a full purchase from product selection to order confirmation

  @smoke @e2e
  Scenario: Complete end-to-end purchase without coupon
    Given I am on the GreenKart home page
    When I add product "Brocolli" to the cart
    And I proceed to the cart
    And I proceed to checkout
    And I accept terms and place the order
    Then the order should be placed successfully

  @e2e @coupon
  Scenario: Complete end-to-end purchase with valid coupon
    Given I am on the GreenKart home page
    When I add product "Brocolli" to the cart
    And I add product "Cauliflower" to the cart
    And I proceed to the cart
    And I proceed to checkout
    And I apply coupon code "rahulshettyacademy"
    And I accept terms and place the order
    Then the order should be placed successfully
    And the order confirmation should display an order ID

  @e2e @scenario-outline
  Scenario Outline: Purchase different products end-to-end
    Given I am on the GreenKart home page
    When I add product "<product>" to the cart
    And I proceed to the cart
    And I proceed to checkout
    And I accept terms and place the order
    Then the order should be placed successfully

    Examples:
      | product     |
      | Brocolli    |
      | Cauliflower |
      | Cucumber    |

  @e2e @multi-product
  Scenario: Purchase multiple products with coupon and verify order
    Given I am on the GreenKart home page
    When I add product "Brocolli" to the cart
    And I add product "Tomato" to the cart
    And I add product "Beetroot" to the cart
    And I proceed to the cart
    And I proceed to checkout
    And I apply coupon code "rahulshettyacademy"
    And I accept terms and place the order
    Then the order should be placed successfully
    And the order confirmation should display an order ID
