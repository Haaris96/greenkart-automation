@smoke @homepage
Feature: GreenKart Home Page
  As a user I want to browse and search for products on the GreenKart home page

  Background:
    Given I am on the GreenKart home page

  @search
  Scenario: Search for a valid product
    When I search for product "Brocolli"
    Then at least 1 product should be displayed
    And the product "Brocolli" should appear in results

  @search
  Scenario: Search for an invalid product returns no results
    When I search for product "XYZ_NONEXISTENT"
    Then no products should be displayed

  @add-to-cart
  Scenario: Add a single product to cart
    When I add product "Brocolli" to the cart
    Then the cart count should be 1

  @add-to-cart @scenario-outline
  Scenario Outline: Add multiple different products to cart one at a time
    When I add product "<product>" to the cart
    Then the cart count should be 1

    Examples:
      | product      |
      | Brocolli     |
      | Cauliflower  |
      | Beetroot     |
      | Carrot       |
      | Cucumber     |

  @search @scenario-outline
  Scenario Outline: Search for various products with partial text
    When I search for product "<searchTerm>"
    Then at least <minResults> product should be displayed

    Examples:
      | searchTerm  | minResults |
      | Broc        | 1          |
      | Cauli       | 1          |
      | Tom         | 1          |
      | Car         | 1          |
