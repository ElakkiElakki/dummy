Feature: SC005 rental car sort and comparison

  Scenario Outline: Execute rental cars SC005 testcase <test_case_id>
    Given user prepares rental cars SC005 scenario with test case "<test_case_id>"
    When user executes rental car sort flow for SC005
    Then rental cars SC005 expected outcome should be validated

    Examples:
      | test_case_id |
      | TC_017       |
      | TC_018       |
      | TC_019       |
      | TC_020       |
