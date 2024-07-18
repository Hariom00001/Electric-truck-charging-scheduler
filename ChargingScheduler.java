

import java.util.*;

class Truck {
    int id;
    int batteryCapacity;
    int currentCharge;

    Truck(int id, int batteryCapacity, int currentCharge) {
        this.id = id;
        this.batteryCapacity = batteryCapacity;
        this.currentCharge = currentCharge;
    }

    int getRemainingCharge() {
        return batteryCapacity - currentCharge;
    }
}

class Charger {
    int id;
    int chargingRate;
    int availableTime;

    Charger(int id, int chargingRate) {
        this.id = id;
        this.chargingRate = chargingRate;
        this.availableTime = 0;
    }
}

class ChargingSchedule {
    int chargerId;
    List<Integer> trucks;

    ChargingSchedule(int chargerId) {
        this.chargerId = chargerId;
        this.trucks = new ArrayList<>();
    }

    void addTruck(int truckId) {
        trucks.add(truckId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(chargerId).append(": ");
        for (int truckId : trucks) {
            sb.append(truckId).append(", ");
        }
        if (!trucks.isEmpty()) {
            sb.setLength(sb.length() - 2);
        }
        return sb.toString();
    }
}

public class ChargingScheduler {

    public static void main(String[] args) {
        List<Truck> trucks = new ArrayList<>(Arrays.asList(
                new Truck(1, 100, 50),
                new Truck(2, 120, 20),
                new Truck(3, 80, 60),
                new Truck(4, 90, 10),
                new Truck(5, 90, 10),
                new Truck(6, 90, 10),
                new Truck(7, 90, 10)
        ));

        List<Charger> chargers = new ArrayList<>(Arrays.asList(
                new Charger(1, 30),
                new Charger(2, 20)
        ));

        int hours = 2;

        Map<Integer, ChargingSchedule> schedule = getChargingSchedule(trucks, chargers, hours);

        for (Map.Entry<Integer, ChargingSchedule> entry : schedule.entrySet()) {
            System.out.println(entry.getValue());
        }
    }

    public static Map<Integer, ChargingSchedule> getChargingSchedule(List<Truck> trucks, List<Charger> chargers, int hours) {
        Map<Integer, ChargingSchedule> schedule = new HashMap<>();
        
        trucks.sort(Comparator.comparingInt(Truck::getRemainingCharge));
        
        for (Charger charger : chargers) {
            schedule.put(charger.id, new ChargingSchedule(charger.id));
        }

        PriorityQueue<Charger> chargerQueue = new PriorityQueue<>(Comparator.comparingInt(c -> c.availableTime));
        chargerQueue.addAll(chargers);

        for (Truck truck : trucks) {
            int requiredEnergy = truck.getRemainingCharge();
            boolean assigned = false;

            for (int i = 0; i < chargers.size(); i++) {
                Charger charger = chargerQueue.poll();
                int chargingTime = (int) Math.ceil((double) requiredEnergy / charger.chargingRate);

                if (chargingTime <= hours - charger.availableTime) {
                    schedule.get(charger.id).addTruck(truck.id);
                    charger.availableTime += chargingTime;
                    chargerQueue.offer(charger);
                    assigned = true;
                    break;
                } else {
                    chargerQueue.offer(charger);
                }
            }

            if (!assigned) {
                for (Charger charger : chargers) {
                    if (hours > charger.availableTime) {
                        schedule.get(charger.id).addTruck(truck.id);
                        charger.availableTime = hours;
                        break;
                    }
                }
            }
        }

        return schedule;
    }
}
