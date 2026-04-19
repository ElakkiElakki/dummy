Feature: SC002 pickup date and time validation

  Scenario Outline: Execute SC002 testcase <test_case_id>
    Given user prepares SC002 scenario with test case "<test_case_id>"
    When user executes pickup date and time flow for SC002
    Then SC002 expected outcome should be validated

    Examples:
      | test_case_id |
      | TC_006       |
      | TC_007       |
