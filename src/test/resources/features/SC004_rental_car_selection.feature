Feature: SC004 rental car selection and details

  Scenario Outline: Execute rental cars SC004 testcase <test_case_id>
    Given user prepares rental cars SC004 scenario with test case "<test_case_id>"
    When user executes rental car selection flow for SC004
    Then rental cars SC004 expected outcome should be validated

    Examples:
      | test_case_id |
      | TC_013       |
      | TC_014       |
      | TC_015       |
      | TC_016       |
