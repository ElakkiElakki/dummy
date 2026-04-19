Feature: SC001 rental car search validation

  Scenario Outline: Execute rental cars SC001 testcase <test_case_id>
    Given user prepares rental cars SC001 scenario with test case "<test_case_id>"
    When user executes rental car search flow for SC001
    Then rental cars SC001 expected outcome should be validated

    Examples:
      | test_case_id |
      | TC_001       |
      | TC_002       |
      | TC_003       |
      | TC_004       |
