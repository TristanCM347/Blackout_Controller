package blackout;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import response.models.EntityInfoResponse;
import response.models.FileInfoResponse;
import utils.Angle;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static utils.MathsHelper.RADIUS_OF_JUPITER;

import java.util.Arrays;

import static blackout.TestHelpers.assertListAreEqualIgnoringOrder;

@TestInstance(value = Lifecycle.PER_CLASS)
public class Task2Tests {
    @Nested
    public class SatelliteMovement {
        @Test
        public void testStandardSatelliteMovement() {
                BlackoutController controller = new BlackoutController();

                controller.createSatellite("Satellite1", "StandardSatellite",
                        100 + RADIUS_OF_JUPITER, Angle.fromDegrees(340));
                assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(340), 100 + RADIUS_OF_JUPITER,
                        "StandardSatellite"), controller.getInfo("Satellite1"));
                controller.simulate();
                assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(337.95), 100 + RADIUS_OF_JUPITER,
                        "StandardSatellite"), controller.getInfo("Satellite1"));
        }

        @Test
        public void testTeleportingMovement() {
                BlackoutController controller = new BlackoutController();

                controller.createSatellite("Satellite1", "TeleportingSatellite", 10000 + RADIUS_OF_JUPITER,
                        Angle.fromDegrees(0));

                controller.simulate();
                Angle anticlockwiseOnFirstMovement = controller.getInfo("Satellite1").getPosition();
                // "0.7169949007406031"
                controller.simulate();
                Angle anticlockwiseOnSecondMovement = controller.getInfo("Satellite1").getPosition();
                // "1.4339898014812062"
                assertTrue(anticlockwiseOnSecondMovement.compareTo(anticlockwiseOnFirstMovement) == 1);

                // It should take 250 simulations to reach theta = 180.
                // Simulate until Satellite1 reaches theta=180
                controller.simulate(250);

                // Verify that Satellite1 is now at theta=0
                assertTrue(controller.getInfo("Satellite1").getPosition().toDegrees() % 360 == 0);

                // check if satellite travels back towards 180 degrees in the opposite direction
                // now should also start travelling clockwise
                controller.simulate();
                Angle clockwiseOnFirstMovement = controller.getInfo("Satellite1").getPosition();
                // "359.2830050992594"
                controller.simulate();
                Angle clockwiseOnSecondMovement = controller.getInfo("Satellite1").getPosition();
                // "358.5660101985188"
                assertTrue(clockwiseOnSecondMovement.compareTo(clockwiseOnFirstMovement) == -1);
                controller.simulate(250);

                // Verify that Satellite1 is now at theta=0
                assertTrue(controller.getInfo("Satellite1").getPosition().toDegrees() % 360 == 0);

                // repeat process to see if it goes back the other way again
                controller.simulate();
                Angle anticlockwiseOnFirstMovement2 = controller.getInfo("Satellite1").getPosition();
                // "0.7169949007406031"
                controller.simulate();
                Angle anticlockwiseOnSecondMovement2 = controller.getInfo("Satellite1").getPosition();
                // "1.4339898014812062"
                assertTrue(anticlockwiseOnSecondMovement2.compareTo(anticlockwiseOnFirstMovement2) == 1);

                // make another tel satellite start at 270 degrees and verify it travels anticlockwise still
                controller.createSatellite("Satellite2", "TeleportingSatellite", 10000 + RADIUS_OF_JUPITER,
                        Angle.fromDegrees(270));

                controller.simulate();
                Angle anticlockwiseOnFirstMovement3 = controller.getInfo("Satellite2").getPosition();
                // "270.7169949007406"
                controller.simulate();
                Angle anticlockwiseOnSecondMovement3 = controller.getInfo("Satellite2").getPosition();
                // "271.4339898014812"
                assertTrue(anticlockwiseOnSecondMovement3.compareTo(anticlockwiseOnFirstMovement3) == 1);
        }

        @Test
        public void testRelayMovementInRange() {
                BlackoutController controller = new BlackoutController();

                controller.createSatellite("Satellite1", "RelaySatellite", 100 + RADIUS_OF_JUPITER,
                        Angle.fromDegrees(180));

                // moves in negative direction as thats the default if in the range
                assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(180), 100 + RADIUS_OF_JUPITER,
                        "RelaySatellite"), controller.getInfo("Satellite1"));
                controller.simulate();
                assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(178.77), 100 + RADIUS_OF_JUPITER,
                        "RelaySatellite"), controller.getInfo("Satellite1"));
                controller.simulate();
                assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(177.54), 100 + RADIUS_OF_JUPITER,
                        "RelaySatellite"), controller.getInfo("Satellite1"));
                controller.simulate();
                assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(176.31), 100 + RADIUS_OF_JUPITER,
                        "RelaySatellite"), controller.getInfo("Satellite1"));

                controller.simulate(5);
                assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(170.18), 100 + RADIUS_OF_JUPITER,
                        "RelaySatellite"), controller.getInfo("Satellite1"));
                controller.simulate(24);
                assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(140.72), 100 + RADIUS_OF_JUPITER,
                        "RelaySatellite"), controller.getInfo("Satellite1"));
                // edge case
                controller.simulate();
                assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(139.49), 100 + RADIUS_OF_JUPITER,
                        "RelaySatellite"), controller.getInfo("Satellite1"));
                // coming back
                controller.simulate(1);
                assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(140.72), 100 + RADIUS_OF_JUPITER,
                        "RelaySatellite"), controller.getInfo("Satellite1"));
                controller.simulate(5);
                assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(146.85), 100 + RADIUS_OF_JUPITER,
                        "RelaySatellite"), controller.getInfo("Satellite1"));
        }

        @Test
        public void testRelayMovementOutsideRange() {
                BlackoutController controller = new BlackoutController();

                // create 2 relay satellites outside of the range
                // positive direction
                controller.createSatellite("Satellite2", "RelaySatellite", 100 + RADIUS_OF_JUPITER,
                        Angle.fromDegrees(345));
                controller.simulate();
                Angle anticlockwiseOnFirstMovement = controller.getInfo("Satellite2").getPosition();
                // "346.2275737993976"
                controller.simulate();
                Angle anticlockwiseOnSecondMovement = controller.getInfo("Satellite2").getPosition();
                // "347.4551475987952"
                assertTrue(anticlockwiseOnSecondMovement.compareTo(anticlockwiseOnFirstMovement) == 1);

                // negative direction
                controller.createSatellite("Satellite3", "RelaySatellite", 100 + RADIUS_OF_JUPITER,
                        Angle.fromDegrees(300));
                controller.simulate();
                Angle clockwiseOnFirstMovement = controller.getInfo("Satellite3").getPosition();
                // "298.7724262006024"
                controller.simulate();
                Angle clockwiseOnSecondMovement = controller.getInfo("Satellite3").getPosition();
                // "297.5448524012049"
                assertTrue(clockwiseOnSecondMovement.compareTo(clockwiseOnFirstMovement) == -1);
        }
    }

    @Nested
    public class CommunicableEntitiesInRange {
        @Test
        public void testEntitiesInRangeMaths() {
                BlackoutController controller = new BlackoutController();

                // create entities and see if they provide the correct entities in range
                controller.createSatellite("Satellite1", "StandardSatellite", 1000 + RADIUS_OF_JUPITER,
                        Angle.fromDegrees(320));
                controller.createSatellite("Satellite2", "StandardSatellite", 1000 + RADIUS_OF_JUPITER,
                        Angle.fromDegrees(315));
                controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(310));
                controller.createDevice("DeviceC", "HandheldDevice", Angle.fromDegrees(320));
                controller.createDevice("DeviceD", "HandheldDevice", Angle.fromDegrees(180));
                controller.createSatellite("Satellite3", "StandardSatellite", 2000 + RADIUS_OF_JUPITER,
                        Angle.fromDegrees(175));

                assertListAreEqualIgnoringOrder(Arrays.asList("DeviceC", "Satellite2"),
                        controller.communicableEntitiesInRange("Satellite1"));
                assertListAreEqualIgnoringOrder(Arrays.asList("DeviceB", "DeviceC", "Satellite1"),
                        controller.communicableEntitiesInRange("Satellite2"));
                assertListAreEqualIgnoringOrder(Arrays.asList("Satellite2"),
                        controller.communicableEntitiesInRange("DeviceB"));

                assertListAreEqualIgnoringOrder(Arrays.asList("DeviceD"),
                        controller.communicableEntitiesInRange("Satellite3"));

                // see if it goes out of range once it moves
                controller.simulate(200);
                assertListAreEqualIgnoringOrder(Arrays.asList(), controller.communicableEntitiesInRange("Satellite3"));

                // see if the device comes back in range
                controller.simulate(100);
                assertListAreEqualIgnoringOrder(Arrays.asList("DeviceB"),
                        controller.communicableEntitiesInRange("Satellite3"));
        }

        @Test
        public void testEntitiesInRangeCompatibilty() {
                BlackoutController controller = new BlackoutController();

                controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(317));
                controller.createDevice("DeviceB", "DesktopDevice", Angle.fromDegrees(320));
                controller.createSatellite("Satellite1", "StandardSatellite", 20000 + RADIUS_OF_JUPITER,
                        Angle.fromDegrees(318));
                controller.createSatellite("Satellite2", "RelaySatellite", 20000 + RADIUS_OF_JUPITER,
                        Angle.fromDegrees(319));

                // check if device comes up when searching for another device using relay
                // check if standard satellite can talk to desktop device and vice versa
                // and do it when relay is near as well
                assertListAreEqualIgnoringOrder(Arrays.asList("Satellite1", "Satellite2"),
                        controller.communicableEntitiesInRange("DeviceA"));
                assertListAreEqualIgnoringOrder(Arrays.asList("Satellite2"),
                        controller.communicableEntitiesInRange("DeviceB"));

                assertListAreEqualIgnoringOrder(Arrays.asList("Satellite2", "DeviceA"),
                        controller.communicableEntitiesInRange("Satellite1"));
                assertListAreEqualIgnoringOrder(Arrays.asList("Satellite1", "DeviceA", "DeviceB"),
                        controller.communicableEntitiesInRange("Satellite2"));
        }

        @Test
        public void testEntitiesInRangeRelaysChainedTogether() {
                BlackoutController controller = new BlackoutController();
                // create 2 non communicable satellites
                // they wont be in range until you add the third satellite
                controller.createSatellite("Start", "StandardSatellite", 100000,
                        Angle.fromDegrees(180));
                controller.createSatellite("End", "StandardSatellite", 100000,
                        Angle.fromDegrees(0));
                assertListAreEqualIgnoringOrder(Arrays.asList(),
                        controller.communicableEntitiesInRange("Start"));

                // add the other connector relay
                controller.createSatellite("Connector", "RelaySatellite", 100000,
                        Angle.fromDegrees(90));
                assertListAreEqualIgnoringOrder(Arrays.asList("Connector", "End"),
                        controller.communicableEntitiesInRange("Start"));

                // add another satellite thats not in range of start
                controller.createSatellite("End2", "StandardSatellite", 80000,
                        Angle.fromDegrees(340));
                assertListAreEqualIgnoringOrder(Arrays.asList("Connector", "End"),
                        controller.communicableEntitiesInRange("Start"));

                // add a second relay satellite (the 2 relays will now chain) so it can be communicated with
                controller.createSatellite("Connector2", "RelaySatellite", 100000,
                        Angle.fromDegrees(0));
                assertListAreEqualIgnoringOrder(Arrays.asList("Connector", "End", "Connector2", "End2"),
                        controller.communicableEntitiesInRange("Start"));

                // prove its actually the addition of the extra relay by removing the first relay
                controller.removeDevice("Connector");
                assertListAreEqualIgnoringOrder(Arrays.asList(),
                        controller.communicableEntitiesInRange("Start"));

        }
    }

    @Nested
    public class FileExceptions {
        @Test
        public void testFileExistsExceptions() {
                BlackoutController controller = new BlackoutController();

                controller.createSatellite("Satellite1", "StandardSatellite", 5000 + RADIUS_OF_JUPITER,
                        Angle.fromDegrees(320));
                controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(310));
                controller.createDevice("DeviceC", "HandheldDevice", Angle.fromDegrees(320));

                // send a file that doesnt exist
                String msg = "Hey";
                controller.addFileToDevice("DeviceC", "FileAlpha", msg);
                assertThrows(FileTransferException.VirtualFileNotFoundException.class,
                        () -> controller.sendFile("NonExistentFile", "DeviceC", "Satellite1"));

                assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "DeviceC", "Satellite1"));
                assertEquals(new FileInfoResponse("FileAlpha", "", msg.length(), false),
                        controller.getInfo("Satellite1").getFiles().get("FileAlpha"));
                controller.simulate(msg.length() * 2);

                // send a file that does exist
                assertThrows(FileTransferException.VirtualFileAlreadyExistsException.class,
                        () -> controller.sendFile("FileAlpha", "DeviceC", "Satellite1"));
        }

        @Test
        public void testBandwidthExceptions() {
                BlackoutController controller = new BlackoutController();

                controller.createSatellite("Satellite1", "StandardSatellite", 5000 + RADIUS_OF_JUPITER,
                        Angle.fromDegrees(320));
                controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(319));
                controller.createDevice("DeviceB", "HandheldDevice", Angle.fromDegrees(320));
                String msg1 = "1";
                String msg2 = "2";

                // try send 2 files to a stand satellite
                controller.addFileToDevice("DeviceA", "File1", msg1);
                assertDoesNotThrow(() -> controller.sendFile("File1", "DeviceA", "Satellite1"));
                controller.addFileToDevice("DeviceA", "File2", msg2);
                assertThrows(FileTransferException.VirtualFileNoBandwidthException.class,
                        () -> controller.sendFile("File2", "DeviceA", "Satellite1"));

                assertEquals(new FileInfoResponse("File1", "", msg1.length(), false),
                        controller.getInfo("Satellite1").getFiles().get("File1"));
                assertEquals(new FileInfoResponse("File1", msg1, msg1.length(), true),
                        controller.getInfo("DeviceA").getFiles().get("File1"));

                controller.simulate();

                assertEquals(new FileInfoResponse("File1", msg1, msg1.length(), true),
                        controller.getInfo("Satellite1").getFiles().get("File1"));

                // now satellite1 has file so it should now be able to recieve another without problem
                assertDoesNotThrow(() -> controller.sendFile("File2", "DeviceA", "Satellite1"));

                assertEquals(new FileInfoResponse("File2", "", msg2.length(), false),
                        controller.getInfo("Satellite1").getFiles().get("File2"));
                assertEquals(new FileInfoResponse("File2", msg2, msg2.length(), true),
                        controller.getInfo("DeviceA").getFiles().get("File2"));

                controller.simulate();

                assertEquals(new FileInfoResponse("File2", msg2, msg2.length(), true),
                        controller.getInfo("Satellite1").getFiles().get("File2"));

                // now satellite has 2 files now try send 2 files from it
                assertDoesNotThrow(() -> controller.sendFile("File1", "Satellite1", "DeviceB"));
                assertThrows(FileTransferException.VirtualFileNoBandwidthException.class,
                        () -> controller.sendFile("File2", "Satellite1", "DeviceB"));

        }

        @Test
        public void testStorageExceptions() {
                BlackoutController controller = new BlackoutController();

                controller.createSatellite("Satellite1", "StandardSatellite", 5000 + RADIUS_OF_JUPITER,
                        Angle.fromDegrees(321));
                controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(319));

                String stringWith201characters = "/var/foldet0s843_c0000gn/T/com.apple.usvityd/"
                + "shared-pasteboard/items/003DDDF5-2061-4E9B-B692-AF3D512C683F/0c07b8b3f384271f4fc"
                + "2aa74f8dffa64fa1.rtfdandnowsomemmoreradndomcharcharcters so i can get to 201";

                controller.addFileToDevice("DeviceA", "File1", stringWith201characters);
                // check max byte storage limit
                // send 201 bytes to standard sat
                assertThrows(FileTransferException.VirtualFileNoStorageSpaceException.class,
                        () -> controller.sendFile("File1", "DeviceA", "Satellite1"));

                // check max file stoarage limit
                // send 3 files to standard then check if it can recieve anbother file
                String msg = "1";
                controller.addFileToDevice("DeviceA", "File2", msg);
                controller.addFileToDevice("DeviceA", "File3", msg);
                controller.addFileToDevice("DeviceA", "File4", msg);
                controller.addFileToDevice("DeviceA", "File5", msg);
                assertDoesNotThrow(() -> controller.sendFile("File2", "DeviceA", "Satellite1"));
                controller.simulate();
                assertDoesNotThrow(() -> controller.sendFile("File3", "DeviceA", "Satellite1"));
                controller.simulate();
                assertDoesNotThrow(() -> controller.sendFile("File4", "DeviceA", "Satellite1"));
                controller.simulate();

                // try add the 4th file
                assertThrows(FileTransferException.VirtualFileNoStorageSpaceException.class,
                        () -> controller.sendFile("File5", "DeviceA", "Satellite1"));
        }
    }

    @Nested
    public class FileTrasferExamples {
        @Test
        public void testBasicTrasnfer() {
                BlackoutController controller = new BlackoutController();

                controller.createSatellite("Satellite1", "StandardSatellite", 10000 + RADIUS_OF_JUPITER,
                        Angle.fromDegrees(320));
                controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(310));
                controller.createDevice("DeviceC", "HandheldDevice", Angle.fromDegrees(320));

                String msg = "Hey";
                controller.addFileToDevice("DeviceC", "FileAlpha", msg);
                assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "DeviceC", "Satellite1"));
                assertEquals(new FileInfoResponse("FileAlpha", "", msg.length(), false),
                        controller.getInfo("Satellite1").getFiles().get("FileAlpha"));

                controller.simulate(msg.length() * 2);
                assertEquals(new FileInfoResponse("FileAlpha", msg, msg.length(), true),
                    controller.getInfo("Satellite1").getFiles().get("FileAlpha"));

                assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "Satellite1", "DeviceB"));
                assertEquals(new FileInfoResponse("FileAlpha", "", msg.length(), false),
                        controller.getInfo("DeviceB").getFiles().get("FileAlpha"));

                controller.simulate(msg.length());
                assertEquals(new FileInfoResponse("FileAlpha", msg, msg.length(), true),
                        controller.getInfo("DeviceB").getFiles().get("FileAlpha"));
        }

        @Test
        public void testOutOfRange() {
                BlackoutController controller = new BlackoutController();
                // create a satellite that goes out of range of the device while uploading
                // then check if the file gets removed from the host
                controller.createSatellite("1", "StandardSatellite", 100000, Angle.fromDegrees(50));
                controller.createDevice("A", "LaptopDevice", Angle.fromDegrees(90));
                String msg = "0123456789";
                controller.addFileToDevice("A", "File1", msg);

                assertListAreEqualIgnoringOrder(Arrays.asList("1"),
                        controller.communicableEntitiesInRange("A"));

                assertDoesNotThrow(() -> controller.sendFile("File1", "A", "1"));
                controller.simulate();
                assertEquals(new FileInfoResponse("File1", "0", msg.length(), false),
                        controller.getInfo("1").getFiles().get("File1"));
                controller.simulate();
                assertEquals(new FileInfoResponse("File1", "01", msg.length(), false),
                        controller.getInfo("1").getFiles().get("File1"));
                controller.simulate();
                assertEquals(new FileInfoResponse("File1", "012", msg.length(), false),
                        controller.getInfo("1").getFiles().get("File1"));
                controller.simulate();
                assertEquals((null),
                        controller.getInfo("1").getFiles().get("File1"));
                // hence file doesnt exist anymore as it got removed once it when out of range
        }

        @Test
        public void testFileProgress() {
                BlackoutController controller = new BlackoutController();

                // test multiple sends and recieves and see if the bandwidths are calculated properly

                controller.createSatellite("Tel", "TeleportingSatellite", 100000, Angle.fromDegrees(260));
                controller.createDevice("A", "LaptopDevice", Angle.fromDegrees(270));
                // public static final int RECIEVE_BANDWIDTH = 15;
                // get telsat to recieve 2 files simultaneously so should recieve 7 per simulate
                String msg1 = "01234567890123456789";
                String msg2 = "98765432109876543210";
                controller.addFileToDevice("A", "File1", msg1);
                controller.addFileToDevice("A", "File2", msg2);
                assertDoesNotThrow(() -> controller.sendFile("File1", "A", "Tel"));
                assertDoesNotThrow(() -> controller.sendFile("File2", "A", "Tel"));
                // check progress to see if bytes transfered corretly
                controller.simulate();
                assertEquals(new FileInfoResponse("File1", "0123456", msg1.length(), false),
                        controller.getInfo("Tel").getFiles().get("File1"));
                assertEquals(new FileInfoResponse("File2", "9876543", msg2.length(), false),
                        controller.getInfo("Tel").getFiles().get("File2"));
                controller.simulate();
                assertEquals(new FileInfoResponse("File1", "01234567890123", msg1.length(), false),
                        controller.getInfo("Tel").getFiles().get("File1"));
                assertEquals(new FileInfoResponse("File2", "98765432109876", msg2.length(), false),
                        controller.getInfo("Tel").getFiles().get("File2"));
                controller.simulate();
                assertEquals(new FileInfoResponse("File1", "01234567890123456789", msg1.length(), true),
                        controller.getInfo("Tel").getFiles().get("File1"));
                assertEquals(new FileInfoResponse("File2", "98765432109876543210", msg2.length(), true),
                        controller.getInfo("Tel").getFiles().get("File2"));

                // public static final int SEND_BANDWIDTH = 10;
                // now get telsat to send the 2 files and see if it splits it appropriately
                // one of the telsat sends is to a standard sat and they can only receive 1 byte per min
                // standard should leave midway through transfer
                // meaning bytes received by remaning trasnfer should increase
                controller.createSatellite("Stand", "StandardSatellite", 100000, Angle.fromDegrees(173));
                controller.createDevice("B", "LaptopDevice", Angle.fromDegrees(270));

                assertDoesNotThrow(() -> controller.sendFile("File1", "Tel", "B"));
                assertDoesNotThrow(() -> controller.sendFile("File2", "Tel", "Stand"));

                assertListAreEqualIgnoringOrder(Arrays.asList("A", "B", "Stand"),
                        controller.communicableEntitiesInRange("Tel"));

                controller.simulate();
                assertListAreEqualIgnoringOrder(Arrays.asList("A", "B", "Stand"),
                        controller.communicableEntitiesInRange("Tel"));
                assertEquals(new FileInfoResponse("File1", "01234", msg1.length(), false),
                        controller.getInfo("B").getFiles().get("File1"));
                assertEquals(new FileInfoResponse("File2", "9", msg2.length(), false),
                        controller.getInfo("Stand").getFiles().get("File2"));

                // now stand goes out of range
                controller.simulate();
                assertListAreEqualIgnoringOrder(Arrays.asList("A", "B"),
                        controller.communicableEntitiesInRange("Tel"));
                assertEquals(new FileInfoResponse("File1", "012345678901234", msg1.length(), false),
                        controller.getInfo("B").getFiles().get("File1"));
                assertEquals(null,
                        controller.getInfo("Stand").getFiles().get("File2"));
        }

        @Test
        public void testTeleportingSatelliteFileTransfering() {
                BlackoutController controller = new BlackoutController();

                // Tests
                // 1. device sends to telsat when telsat teleports
                // 2. telsat sends to device when telsat teleports
                // 3. sat sends to telsat when telsat teleports
                // 4. telsat sends to sat when telsat teleports

                // make telsat have 2 files
                // make sat have 1 file
                controller.createSatellite("Stand", "StandardSatellite", 100000, Angle.fromDegrees(185));
                controller.createDevice("A", "DesktopDevice", Angle.fromDegrees(180));
                controller.createDevice("B", "LaptopDevice", Angle.fromDegrees(180));
                String msg1 = "1t2t3t4t5t6t";
                String msg2 = "titatot";
                String msg3 = "touchtasteteethtumble";
                controller.addFileToDevice("A", "File1", msg1);
                controller.addFileToDevice("B", "File2", msg2);
                controller.addFileToDevice("A", "File3", msg1);
                controller.addFileToDevice("B", "File4", msg3);

                assertDoesNotThrow(() -> controller.sendFile("File2", "B", "Stand"));
                controller.simulate(7);

                controller.createSatellite("Tel", "TeleportingSatellite", 100000, Angle.fromDegrees(178));
                assertDoesNotThrow(() -> controller.sendFile("File1", "A", "Tel"));
                assertDoesNotThrow(() -> controller.sendFile("File3", "A", "Tel"));
                controller.simulate(2);

                // teleport sat is 2 simulates away from teleporting
                assertDoesNotThrow(() -> controller.sendFile("File1", "Tel", "B"));
                assertDoesNotThrow(() -> controller.sendFile("File3", "Tel", "Stand"));

                assertDoesNotThrow(() -> controller.sendFile("File4", "B", "Tel"));
                assertDoesNotThrow(() -> controller.sendFile("File2", "Stand", "Tel"));

                controller.simulate(1);

                // check all file progress, bytes and content
                assertEquals(new FileInfoResponse("File1", "1t2t3", msg1.length(), false),
                        controller.getInfo("B").getFiles().get("File1"));

                assertEquals(new FileInfoResponse("File3", "1", msg1.length(), false),
                        controller.getInfo("Stand").getFiles().get("File3"));

                assertEquals(new FileInfoResponse("File2", "t", msg2.length(), false),
                        controller.getInfo("Tel").getFiles().get("File2"));

                // control check corruptions on other original files
                assertEquals(new FileInfoResponse("File1", "1t2t3t4t5t6t", msg1.length(), true),
                        controller.getInfo("Tel").getFiles().get("File1"));

                assertEquals(new FileInfoResponse("File3", "1t2t3t4t5t6t", msg1.length(), true),
                        controller.getInfo("Tel").getFiles().get("File3"));

                assertEquals(new FileInfoResponse("File2", "titatot", msg2.length(), true),
                        controller.getInfo("Stand").getFiles().get("File2"));

                // device send to telsat circumstance(corruption)
                assertEquals(new FileInfoResponse("File4", "touchtasteteethtumble", msg3.length(), true),
                        controller.getInfo("B").getFiles().get("File4"));

                assertEquals(new FileInfoResponse("File4", "touchta", msg3.length(), false),
                        controller.getInfo("Tel").getFiles().get("File4"));

                assertNotEquals(controller.getInfo("Tel").getPosition(), Angle.fromDegrees(0));
                controller.simulate(1);
                assertEquals(controller.getInfo("Tel").getPosition(), Angle.fromDegrees(0));
                // satellite has teleported now
                // check all file progress, bytes and content
                assertEquals(new FileInfoResponse("File1", "1t2t3456", msg1.length() - 4, true),
                        controller.getInfo("B").getFiles().get("File1"));

                assertEquals(new FileInfoResponse("File3", "123456", msg1.length() - 6, true),
                        controller.getInfo("Stand").getFiles().get("File3"));

                assertEquals(new FileInfoResponse("File2", "tiao", msg2.length() - 3, true),
                        controller.getInfo("Tel").getFiles().get("File2"));

                // control check corruptions on other original files
                assertEquals(new FileInfoResponse("File1", "1t2t3t4t5t6t", msg1.length(), true),
                        controller.getInfo("Tel").getFiles().get("File1"));

                assertEquals(new FileInfoResponse("File3", "1t2t3t4t5t6t", msg1.length(), true),
                        controller.getInfo("Tel").getFiles().get("File3"));

                assertEquals(new FileInfoResponse("File2", "titatot", msg2.length(), true),
                        controller.getInfo("Stand").getFiles().get("File2"));

                // device send to telsat circumstance(corruption)
                assertEquals(new FileInfoResponse("File4", "ouchaseeehumble", msg3.length() - 6, true),
                        controller.getInfo("B").getFiles().get("File4"));

                assertEquals(null,
                        controller.getInfo("Tel").getFiles().get("File4"));
        }
    }
}

