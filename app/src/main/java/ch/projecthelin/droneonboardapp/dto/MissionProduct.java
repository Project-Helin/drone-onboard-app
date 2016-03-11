package ch.projecthelin.droneonboardapp.dto;

public class MissionProduct {
    private Mission mission;
    private Product product;
    private int amount;

    public MissionProduct() {

    }

    public MissionProduct(Mission mission, Product product, int amount) {
        this.mission = mission;
        this.product = product;
        this.amount = amount;
    }

    public Mission getMission() {
        return mission;
    }

    public void setMission(Mission mission) {
        this.mission = mission;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
