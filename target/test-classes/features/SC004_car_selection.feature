Feature: SC004 car selection and unavailable car validation

  Scenario Outline: Execute SC004 testcase <test_case_id>
    Given user prepares SC004 scenario with test case "<test_case_id>"
    When user executes car selection flow for SC004
    Then SC004 expected outcome should be validated

    Examples:
      | test_case_id |
      | TC_014       |
      | TC_015       |
      | TC_016       |
      | TC_017       |
