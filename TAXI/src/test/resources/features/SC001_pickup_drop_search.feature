Feature: SC001 pickup and drop validation

  Scenario Outline: Execute SC001 testcase <test_case_id>
    Given user prepares SC001 scenario with test case "<test_case_id>"
    When user executes pickup and drop search for SC001
    Then SC001 expected outcome should be validated

    Examples:
      | test_case_id |
      | TC_001       |
      | TC_002       |
      | TC_003       |
      | TC_004       |
      | TC_005       |
