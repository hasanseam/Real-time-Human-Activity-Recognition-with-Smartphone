# System Usability Scale (SUS) Questionnaire
**Target Audience:** Technical Personnel & Researchers (Sensor Data Collection)
**System:** Real-time Human Activity Recognition (HAR) Data Collection App

## Instructions for Researchers
Please evaluate the application based on your experience setting up the sensors, configuring activities, and collecting data. For each of the following statements, mark the box that best describes your reaction.

**Scale:**
1 = Strongly Disagree
2 = Disagree
3 = Neutral
4 = Agree
5 = Strongly Agree

---

### Standard SUS Questions
*(Note: These 10 questions must remain exactly as worded to calculate a standardized SUS score).*

1. I think that I would like to use this application frequently for my data collection needs.
   [ 1 ]  [ 2 ]  [ 3 ]  [ 4 ]  [ 5 ]

2. I found the application unnecessarily complex.
   [ 1 ]  [ 2 ]  [ 3 ]  [ 4 ]  [ 5 ]

3. I thought the application was easy to use.
   [ 1 ]  [ 2 ]  [ 3 ]  [ 4 ]  [ 5 ]

4. I think that I would need the support of a developer/technical person to be able to use this system.
   [ 1 ]  [ 2 ]  [ 3 ]  [ 4 ]  [ 5 ]

5. I found the various functions in this application (sensor configuration, activity setup, data export) were well integrated.
   [ 1 ]  [ 2 ]  [ 3 ]  [ 4 ]  [ 5 ]

6. I thought there was too much inconsistency in this application.
   [ 1 ]  [ 2 ]  [ 3 ]  [ 4 ]  [ 5 ]

7. I would imagine that most researchers would learn to use this application very quickly.
   [ 1 ]  [ 2 ]  [ 3 ]  [ 4 ]  [ 5 ]

8. I found the application very cumbersome to use.
   [ 1 ]  [ 2 ]  [ 3 ]  [ 4 ]  [ 5 ]

9. I felt very confident using the application to record accurate sensor data.
   [ 1 ]  [ 2 ]  [ 3 ]  [ 4 ]  [ 5 ]

10. I needed to learn a lot of things before I could get going with this application.
    [ 1 ]  [ 2 ]  [ 3 ]  [ 4 ]  [ 5 ]

---

### Additional Researcher-Specific Feedback (Optional)
*(These questions do not count towards the standard SUS score but provide qualitative feedback for technical users).*

11. **Data Accuracy & Fidelity:** Did the exported CSV/data files meet your requirements for sampling rate and precision?
    - [ ] Yes
    - [ ] No
    - **Comments:** _________________________________________

12. **Background Collection:** Did you experience any data loss when the application was running in the background?
    - [ ] Yes
    - [ ] No
    - **Comments:** _________________________________________

13. **Configuration Flexibility:** Was the process of configuring custom activities and selecting specific sensor channels (e.g., Accelerometer vs Gyroscope) intuitive enough for a research setting?
    - [ ] Yes
    - [ ] No
    - **Comments:** _________________________________________

---

## How to Calculate the SUS Score
1. For odd-numbered questions (1, 3, 5, 7, 9): Subtract 1 from the user's response. *(Score = Response - 1)*
2. For even-numbered questions (2, 4, 6, 8, 10): Subtract the user's response from 5. *(Score = 5 - Response)*
3. Add up the calculated scores for all 10 questions.
4. Multiply the total sum by **2.5** to get the final SUS score (ranging from 0 to 100).

*A score above 68 is generally considered above average for usability.*
