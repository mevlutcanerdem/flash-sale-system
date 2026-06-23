package com.flashsale.order_service.dto;

public class OrderEvent {

        private String orderNumber;
        private String userId;
        private Double amount;

        //  Boş constructor  (SpringBoot JSON a çevirirken buna ihtiyaç duyar)
        public OrderEvent(){
        }

        // Dolu constructor
    public OrderEvent(String orderNumber,String userId, Double amount){
            this.orderNumber = orderNumber;
            this.userId = userId;
            this.amount = amount;
    }
        // lombok hatasıyla uğraşmamak için getter ve setterlar manuel olarak yazıyorum


    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
