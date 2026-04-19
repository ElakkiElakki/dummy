Feature: SC006 rental car result content and search refinement

  Scenario Outline: Execute rental cars SC006 testcase <test_case_id>
    Given user prepares rental cars SC006 scenario with test case "<test_case_id>"
    When user executes rental car refinement flow for SC006
    Then rental cars SC006 expected outcome should be validated

    Examples:
      | test_case_id |
      | TC_021       |
      | TC_022       |
      | TC_023       |
      | TC_024       |
