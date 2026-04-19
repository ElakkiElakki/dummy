Feature: SC006 booking details form validation

  Scenario Outline: Execute SC006 testcase <test_case_id>
    Given user prepares SC006 scenario with test case "<test_case_id>"
    When user executes booking details flow for SC006
    Then SC006 expected outcome should be validated

    Examples:
      | test_case_id |
      | TC_022       |
      | TC_023       |
      | TC_024       |
      | TC_025       |
      | TC_026       |
