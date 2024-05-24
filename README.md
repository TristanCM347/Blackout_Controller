# BlackoutController
Simulates satellites communicating and functioning with each other and various devices around Jupiter. Varieties of file transfers are available for different satellites and devices.

- a desgin.pdf file is attached for a summary of the design of the project

# Overview

This project is a simulation of various entities orbiting around Jupiter, including devices like desktops, laptops, and handheld devices, as well as satellites like standard satellites, relay satellites, and teleporting satellites. The simulation models the movement, communication, and data storage capabilities of these entities.

## Features

- **Diverse Entities**: Simulate different types of entities, including desktop devices, handheld devices, laptops, standard satellites, relay satellites, and teleporting satellites, each with unique properties and functionalities.
- **Movement Simulation**: Each satellite type has its own movement pattern, including standard linear motion, relay satellite movement with direction changes based on position, and teleporting satellites that can change position instantaneously under specific conditions.
- **Communication and Bandwidth Management**: Entities have the ability to send and receive data, with bandwidth limitations simulated according to the entity type. This includes the simulation of data transfers, file storage capacities, and special conditions for data transfer in teleporting satellites.
- **Compatibility Checks**: The system includes mechanisms to check compatibility between different entities, ensuring that certain types of communication or interaction are restricted based on entity type (e.g., desktop devices not being compatible with standard satellites).
- **Environmental Simulation**: The simulation takes into account the environmental conditions of Jupiter, including its radius for calculating movements and positions of the entities in its orbit.

## How It Works

### Entities

Each entity in the simulation (devices and satellites) extends from a base Entity class, inheriting common properties such as ID, position, and range. Devices and satellites have specific properties and behaviors:

- **Devices (Desktop, Handheld, Laptop)**: These entities are characterized by their communication range, with desktop devices having the largest range and handheld devices the smallest. Desktop devices also have a unique compatibility check to prevent communication with standard satellites.
- **Satellites (Standard, Relay, Teleporting)**: Satellites move around Jupiter, with each type having a unique movement pattern and communication capabilities. Standard satellites move in a fixed direction, relay satellites adjust their movement based on their position relative to Jupiter, and teleporting satellites can change their position under specific conditions while also having unique data transfer capabilities.

### Movement

Satellites implement the Movement interface, allowing them to move according to their specific rules. Movement is simulated based on the satellite's speed, direction, and the gravitational effects of Jupiter.

### Communication

Entities can send and receive data, with each type having predefined bandwidth for these activities. The simulation includes mechanisms for transferring data between entities, taking into account their range, compatibility, and bandwidth limitations.

### Simulation Loop

The core of the simulation is the loop that updates the positions of the satellites, checks for communication opportunities, and manages data transfers. This loop allows the simulation to dynamically evolve over time, representing the complex interactions between the entities orbiting Jupiter.








## Testing ##
Test 1-1: Test creating different types of devices
SUCCESS DONE

Test 1-2: Test removing different types of devices
SUCCESS DONE

Test 1-3: Test creating different types of satellites
SUCCESS DONE

Test 1-4: Test removing different types of satellites
SUCCESS DONE

Test 1-5: Test adding a file to a device
SUCCESS DONE

Test 1-6: Testing retrieving device information
SUCCESS DONE

Test 1-7: Testing retrieving satellite information
SUCCESS DONE

Test 2-1: Test simulation with a standard satellite
SUCCESS DONE

Test 2-3: Test wrapping behaviour around 360 and 0 degrees with standard satellite
SUCCESS DONE

Test 2-4: Test wrapping behaviour around 360 and 0 degrees with teleporting satellite
SUCCESS DONE

Test 2-5: Test movement of relay satellites
SUCCESS DONE

Test 2-6: Test relay satellites choose the shortest path to get into the [140-190] region
SUCCESS DONE

Test 2-7: Test communicable entities in range for different types of devices and satellites
SUCCESS DONE

Test 2-8: Testing a file can be created and sent
SUCCESS DONE

Test 2-9: Testing simple file transfer from device to satellite
SUCCESS DONE

Test 2-10: Testing simple file transfer from satellite to device
SUCCESS DONE

Test 2-11: Test multiple files transferring simultaneously
SUCCESS DONE

Test 2-12: Test teleporting satellite transmitting to devices - splitting bandwidth
SUCCESS DONE

Test 2-13: Test teleporting satellite transmitting to standard - bandwidth bottlenecked on standard
SUCCESS DONE

Test 2-14: Test teleporting satellite movement and teleportation behaviour
SUCCESS DONE

Test 2-15: Testing a device sending a file to a teleporting satellite which teleports mid-transfer
SUCCESS DONE

Test 2-16a: Testing file transfer from teleporting satelllite to device
SUCCESS DONE

Test 2-16b: Testing file transfer from teleporting satellite to satellite
SUCCESS DONE

Test 2-16c: Testing file transfer from standard satellite to teleporting
SUCCESS DONE

Test 2-17: Test file-sending behaviour of a teleporting satellite after teleportation is as normal
SUCCESS DONE

Test 2-18: Test entities in range with relay satellites - no movement
Task2MarkingTests > Test entities in range with relay satellites - no movement FAILED org.opentest4j.AssertionFailedError: expected: <true> but was: <false> at app//org.junit.jupiter.api.AssertionUtils.fail(AssertionUtils.java:55) at app//org.junit.jupiter.api.AssertTrue.assertTrue(AssertTrue.java:40) at app//org.junit.jupiter.api.AssertTrue.assertTrue(AssertTrue.java:35) at app//org.junit.jupiter.api.Assertions.assertTrue(Assertions.java:179) at app//blackout.TestHelpers.assertListAreEqualIgnoringOrder(TestHelpers.java:14) at app//blackout.Task2MarkingTests.testHardRelaySatellites(Task2MarkingTests.java:645) FAILURE DONE 1 test completed, 1 failed FAILURE: Build failed with an exception. bin blog.md build build.gradle checkstyle.xml design.pdf design.png gradle gradlew gradlew.bat README.md src UML.drawio What went wrong: Execution failed for task ':test'. > There were failing tests. See the report at: file:///home/carl/tutoring/23T2/automarking/assignment-i-automarking/students/z5420825/build/reports/tests/test/index.html bin blog.md build build.gradle checkstyle.xml design.pdf design.png gradle gradlew gradlew.bat README.md src UML.drawio Try: Run with --stacktrace option to get the stack trace. Run with --info or --debug option to get more log output. Run with --scan to get full insights. bin blog.md build build.gradle checkstyle.xml design.pdf design.png gradle gradlew gradlew.bat README.md src UML.drawio Get more help at https://help.gradle.org BUILD FAILED in 3s

Test 2-19: Test a simple case with recursive relay satellites
SUCCESS DONE

Test 2-20: Test a successful relay file transfer
SUCCESS DONE

Test 2-21: Test sending a file from device to a satellite - satellite moves out of range
SUCCESS DONE

Test 2-22: Test sending a file from a satellite to a device - satellite moves out of range
SUCCESS DONE

Test 2-23: Test file not found exception
SUCCESS DONE

Test 2-24: Test file already exists exception
SUCCESS DONE

Test 2-25: Test no bandwidth exception
SUCCESS DONE

Test 2-26: Test max out storage space on satellite
SUCCESS DONE
