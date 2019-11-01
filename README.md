**Notice**: A website of our researches on Feature Selection is still under construction and will be launched publicly soon. Currently, we only provide core java implementations in this project.

# featureSelectionExperiments4CNEC

This is a *Maven* project implemented using *Java* language for Feature Selection Experiments. As the name implies, this project is created for some **Feature Selection** experiments. Specifically, this one is for CNEC experiments. 

Main structure of the project: 

	.
	+--src
	  +--main
	    +--java
	      +--basic
	      |  +--...  // some basic utilities
	      +--featureSelection
	      |  +--repository
	      |  |  +--frame
	      |  |  +--implement
	      |  |     +--algorithm
	      |  |     +--support
	      |  |     +--model
	      |  +--statistics
	      |  +--utils
	      |     +--...  // some utilities for implemented feature selection algorithms.
	      +--tester
	         +--impl
	            +--heuristic
	               +--quickReduct
	               
All implemented Feature Selection algorithms are at the path 
- *./src/main/java/featureSelection/repository/implement*. 

For algorithm implementations, all codes are at the path
- *./.../featureSelection/repository/implement/algorithm*. 

Codes of feature significance calculations as well as other calculations are at the path 
- *./.../featureSelection/repository/implement/support*.

Relevant entities and models used in algorithms are at the path 
- *./.../featureSelection/repository/implement/model*.

Bases on implemented Feature Selection algorithms and calculations, the complete Heuristic Feature Selection procedures are coded in what we called *testers* respectively at the path
- *./.../tester/impl/heuristic/quickReduct*.

Examples of Feature Selection executions using testers as well as serving as JUnit tests are coded at the path
- *./src/test/java/tester/impl/heuristic/quickReduct*.

-----

In this project, algorithms listed below refer to published papers were **implemented based on the pseudo codes** from the original papers:

1. **Conventional method**, implemented based on:
    - ["Computational Intelligence and Feature Selection: Rough and Fuzzy Approaches"](https://ieeexplore.ieee.org/book/5236578) by Richard Jensen, Qiang Shen.
2. **Dependency based Feature Selection algorithms**:
    - Incremental Dependency Calculation(IDC), implemented as **IDC** based on: 
        ["An incremental dependency calculation technique for feature selection using rough sets"](https://www.sciencedirect.com/science/article/pii/S0020025516000785) by Muhammad Summair Raza, Usman Qamar.
    - Heuristic based Dependency Calculation(HDC), implemented as **HDC** based on: 
        ["A heuristic based dependency calculation technique for rough set theory"](https://www.sciencedirect.com/science/article/abs/pii/S0031320318301432) by Muhammad Summair Raza, Usman Qamar.
    - Direct Dependency Calculation(DDC), implemented as **DDC** based on: 
        ["Feature selection using rough set-based direct dependency calculation by avoiding the positive region"](https://www.sciencedirect.com/science/article/abs/pii/S0888613X17300178) by Muhammad Summair Raza, Usman Qamar.
3. **Positive Approximation Accelerator for attribute reduction**, implemented as **ACC** based on:
    - ["Positive approximation: An accelerator for attribute reduction in rough set theory"](https://doi.org/10.1016/j.artint.2010.04.018) by Yuhua Qian, Jiye Liang, etc..
4. **Compacted decision tables based attribute reduction**, implemented as **CT** based on:
    - ["Compacted decision tables based attribute reduction"](http://dx.doi.org/10.1016/j.knosys.2015.06.013) by Wei Wei, Junhong Wang, Jiye Liang, etc..
5. **Discernibility based Attribute Reduction**, implemented as **FAR-DV** based on:
    - ["Efficient attribute reduction from the viewpoint of discernibility"](https://linkinghub.elsevier.com/retrieve/pii/S0020025515005605) by Shu-Hua Teng, Min Lu, A-Feng Yang, Jun Zhang, Yongjian Nian, Mi He.
6. **Semi-supervised Representative Feature Selection**, implemented as **SRFS** based on:
    - ["An efficient semi-supervised representatives feature selection algorithm based on information theory"](https://linkinghub.elsevier.com/retrieve/pii/S0031320316302242) by Yintong Wang, Jiandong Wang, Hao Liao, Haiyan Chen.
7. **Classified Nested Equivalent Class based Feature Selection**, implemented as **CNEC**
    - Originally designed and implemented. Name of the algorithm has been changed from "*Incremental Decision Rough Equivalent Class(ID-REC)*" into "*Classified Nested Equivalent Class(CNEC)*"

-----

Codes of this project mainly focus on implementations and executions of the Feature Selection algorithm. So no *GUI* or other *interfaces* is provided here. However, to use them in *Java*, you can follow the instructions below:

### Begin to use
  Assuming we want to use codes in this project with minimum change to execute Feature Selection on the universe in the file "**./dataset/demo.csv**" which has 4 conditional attributes and 1 decision attribute, with instance values in the order of *a*<sub>1</sub>, *a*<sub>2</sub>, *a*<sub>3</sub>, *a*<sub>4</sub>, *D*:
  
  - U<sub>1</sub>: 1, 1, 1, 1, 0
  - U<sub>2</sub>: 2, 2, 1, 1, 0
  - U<sub>3</sub>: 1, 1, 1, 1, 0
  - U<sub>4</sub>: 1, 3, 1, 3, 0
  - U<sub>5</sub>: 2, 2, 1, 1, 1
  - U<sub>6</sub>: 3, 1, 2, 1, 0
  - U<sub>7</sub>: 2, 2, 3, 2, 2
  - U<sub>8</sub>: 2, 3, 2, 2, 3
  - U<sub>9</sub>: 3, 1, 2, 1, 1
  - U<sub>10</sub>: 2, 2, 3, 2, 2
  - U<sub>11</sub>: 3, 1, 2, 1, 1
  - U<sub>12</sub>: 2, 3, 2, 2, 3
  - U<sub>13</sub>: 4, 3, 4, 2, 1
  - U<sub>14</sub>: 2, 2, 3, 2, 2
  - U<sub>15</sub>: 4, 3, 4, 2, 2
  
So, the plain content of the csv file(.csv) or a text file(.txt) that contains the above universe should be:
 
```csv
1,1,1,1,0
2,2,1,1,0
1,1,1,1,0
1,3,1,3,0
2,2,1,1,1
3,1,2,1,0
2,2,3,2,2
2,3,2,2,3
3,1,2,1,1
2,2,3,2,2
3,1,2,1,1
2,3,2,2,3
4,3,4,2,1
2,2,3,2,2
4,3,4,2,2
```

**Step 1**: Set the file.

Open the **BasicTester.java** file at the path "*./src/test/java/tester*" and set the variable "*dataFile*" by replacing the String with the file path at **line 41**. 

```java
private static File dataFile = new File("./dataset/demo.csv"); // <- "./dataset/demo.csv"
```


**Step 2**: Choose a Feature Selection algorithm to execute.

At the path "*./src/test/java/tester/impl/heuristic/quickReduct*", examples of Feature Selection algorithm execution demos are provided. So, all we need to do is to select the algorithm and execute it using JUnit.

Supposed we want to execute ACC with core using Shannon Entropy. All we need to do is to open the file "*PositiveApproximationAcceleratorOriginalAlgorithmHeuristicQRTesterTest.java*" at "*tester/impl/heuristic/quickReduct/positiveApproximationAccelerator/original*", set the variable "byCore" into "true" and execute the method "testExecSCE()" by JUnit: 

```java
private boolean byCore = true;

@Test	// execute this method by JUnit.
void testExecSCE() throws Exception {
	// ......
}
```
Finally, we can see the reduct on console as well as other content:

```java
result : [1, 3]		<<= the reduct is printed here !!!
other outputs here..
```
And the result "[1, 3]" means the reduct it found was [a<sub>1</sub>, a<sub>3</sub>].

Here are the full class paths of the implemented algorithm demos:
- **ACC**: *tester.impl.heuristic.quickReduct.positiveApproximationAccelerator.original.PositiveApproximationAcceleratorOriginalAlgorithmHeuristicQRTesterTest*
- **CT**: *tester.impl.heuristic.quickReduct.compactedDecisionTable.original.CompactedDecisionTableHeuristicQRTesterTest*
- **IDC**: *tester.impl.heuristic.quickReduct.dependencyCalculation.IncrementalDependencyCalculationAlgorithmHeuristicQRTesterTest*
- **HDC**: *tester.impl.heuristic.quickReduct.dependencyCalculation.HeuristicDependencyCalculationAlgorithmHeuristicQRTesterTest*
- **DDC**: *tester.impl.heuristic.quickReduct.dependencyCalculation.DirectDependencyCalculationAlgorithmHeuristicQRTesterTest*
- **FAR-DV**: *tester.impl.heuristic.quickReduct.discernibilityView.TengDiscernibilityViewHeuristicQRTesterTest*
- **SRFS**: *tester.impl.heuristic.quickReduct.semisupervisedRepresentative.SemisupervisedRepresentativesFeatureSelectionHeuristicQRTesterTest*
- **CNEC**: *tester.impl.heuristic.quickReduct.roughEquivalentClassBased.original.extension.incrementalDecision.RoughEquivalentClassBasedAlgorithmIncrementalDecisionHeuristicQRTesterTest*
