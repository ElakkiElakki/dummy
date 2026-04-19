Feature: SC003 rental car result filters

  Scenario Outline: Execute rental cars SC003 testcase <test_case_id>
    Given user prepares rental cars SC003 scenario with test case "<test_case_id>"
    When user executes rental car filter flow for SC003
    Then rental cars SC003 expected outcome should be validated

    Examples:
      | test_case_id |
      | TC_009       |
      | TC_010       |
      | TC_011       |
      | TC_012       |
