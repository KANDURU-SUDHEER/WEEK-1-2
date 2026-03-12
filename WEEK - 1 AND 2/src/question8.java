import java.util.*;

class ParkingLotSystem {

    enum Status {
        EMPTY, OCCUPIED, DELETED
    }

    class ParkingSpot {
        String licensePlate;
        long entryTime;
        Status status;

        ParkingSpot() {
            status = Status.EMPTY;
        }
    }

    private ParkingSpot[] table;
    private int capacity;
    private int size = 0;
    private int totalProbes = 0;

    public ParkingLotSystem(int capacity) {
        this.capacity = capacity;
        table = new ParkingSpot[capacity];

        for (int i = 0; i < capacity; i++) {
            table[i] = new ParkingSpot();
        }
    }

    private int hash(String licensePlate) {
        return Math.abs(licensePlate.hashCode()) % capacity;
    }

    public void parkVehicle(String licensePlate) {

        int index = hash(licensePlate);
        int probes = 0;

        while (table[index].status == Status.OCCUPIED) {
            index = (index + 1) % capacity;
            probes++;
        }

        table[index].licensePlate = licensePlate;
        table[index].entryTime = System.currentTimeMillis();
        table[index].status = Status.OCCUPIED;

        size++;
        totalProbes += probes;

        System.out.println("parkVehicle(\"" + licensePlate + "\") → Assigned spot #" +
                index + " (" + probes + " probes)");
    }

    public void exitVehicle(String licensePlate) {

        int index = hash(licensePlate);

        while (table[index].status != Status.EMPTY) {

            if (table[index].status == Status.OCCUPIED &&
                    table[index].licensePlate.equals(licensePlate)) {

                long durationMillis = System.currentTimeMillis() - table[index].entryTime;
                double hours = durationMillis / (1000.0 * 60 * 60);

                double fee = Math.ceil(hours) * 5; // $5 per hour

                table[index].status = Status.DELETED;
                size--;

                System.out.println("exitVehicle(\"" + licensePlate + "\") → Spot #" + index +
                        " freed, Duration: " +
                        String.format("%.2f", hours) + "h, Fee: $" + fee);

                return;
            }

            index = (index + 1) % capacity;
        }

        System.out.println("Vehicle not found.");
    }

    public int findNearestAvailable() {

        for (int i = 0; i < capacity; i++) {
            if (table[i].status == Status.EMPTY) {
                return i;
            }
        }

        return -1;
    }

    public void getStatistics() {

        double occupancy = (size * 100.0) / capacity;
        double avgProbes = size == 0 ? 0 : (double) totalProbes / size;

        System.out.println("\nParking Statistics:");
        System.out.println("Occupancy: " + String.format("%.2f", occupancy) + "%");
        System.out.println("Average Probes: " + String.format("%.2f", avgProbes));
    }

    public static void main(String[] args) throws Exception {

        ParkingLotSystem lot = new ParkingLotSystem(500);

        lot.parkVehicle("ABC-1234");
        lot.parkVehicle("ABC-1235");
        lot.parkVehicle("XYZ-9999");

        Thread.sleep(2000);

        lot.exitVehicle("ABC-1234");

        lot.getStatistics();

        System.out.println("Nearest free spot: " + lot.findNearestAvailable());
    }
}
