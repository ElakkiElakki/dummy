Feature: SC003 search results and no-results behavior

  Scenario Outline: Execute SC003 testcase <test_case_id>
    Given user prepares SC003 scenario with test case "<test_case_id>"
    When user executes search results flow for SC003
    Then SC003 expected outcome should be validated

    Examples:
      | test_case_id |
      | TC_010       |
      | TC_011       |
      | TC_012       |
      | TC_013       |
