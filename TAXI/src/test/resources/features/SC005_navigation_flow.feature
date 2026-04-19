Feature: SC005 navigation flow and browser back behavior

  Scenario Outline: Execute SC005 testcase <test_case_id>
    Given user prepares SC005 scenario with test case "<test_case_id>"
    When user executes navigation flow for SC005
    Then SC005 expected outcome should be validated

    Examples:
      | test_case_id |
      | TC_018       |
      | TC_019       |
      | TC_020       |
      | TC_021       |
