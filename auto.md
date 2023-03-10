# How Autonomous Works:
**Pretzel:**
![Pretzel](pretzel.jpg)

## The Pretzel Explanation

- 1, 2, 3 are scoreing locations
    - 1 and 3 are the outside cone nodes
    - 2 is the middle shelf
- A and C are pickup locations
- B is the Charge Station

## How to Use the Pretzel

- Each location is attached to an action
  - At 1, 2, and 3 the robot scores
  - The robot will intake at A and C
  - The robot will balance at B
- In Shuffleboard we can input Auto sequences, such as:
  - **2B** - score at 2, balance
  - **3A1** - score at 3, pickup at A, score at 1
  - **2AB** - score 2, pickup at A, balance
- However, sometimes we don't want to do an action (in case of mechanical failure or other reasons). For this, we have **"!"**. For example:
  - **2B** - score at 2, balance
  - **OR !2B** - Do not score at 2, balance
  - **3!AB** - Score at 3, do not intake at A, balance

