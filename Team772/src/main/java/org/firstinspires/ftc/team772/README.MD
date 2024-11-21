# Source

Each section has a purpose.

- abstractions
	- Abstractions are interfaces that the OpModes use to control the robot. implementation should implement these.
- opmodes
	- opmodes are the entrance point of the program -- the highest level. OpModes and their helpers (found in helpers and autos) should access abstractions in order to control the robot hardware. opmodes should be hardware-independent in order to allow the entire chassis to be replaced and maintain the same high level logic.
- implementation
	- implementation contains hardware-dependent code. the `drive(x,y,theta)`, for example, is implemented here. While other classes should be able to operate with any type of hardware, classes in drive directly access hardware functions.
- autos
	- autos contains trajectories and paths to be run in Autonomous OpModes. It should not contain any actual logic.
- helpers
	- helpers contains classes and functions to assist OpModes. They should also be hardware-independent.

opmodes - helpers
       V
  abstractions
       V
 implementation
![](structure.png "Image")