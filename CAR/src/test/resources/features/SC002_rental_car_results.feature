Feature: SC002 rental car results and navigation

  Scenario Outline: Execute rental cars SC002 testcase <test_case_id>
    Given user prepares rental cars SC002 scenario with test case "<test_case_id>"
    When user executes rental car results flow for SC002
    Then rental cars SC002 expected outcome should be validated

    Examples:
      | test_case_id |
      | TC_005       |
      | TC_006       |
      | TC_007       |
      | TC_008       |
