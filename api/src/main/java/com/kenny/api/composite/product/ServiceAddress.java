package com.kenny.api.composite.product;

public class ServiceAddress {
        private String compositeAddress;
        private String productAddress;
        private String reviewAddress;
        private String recommendationAddress;

        public ServiceAddress(String compositeAddress, String productAddress, String reviewAddress, String recommendationAddress) {
            this.compositeAddress = compositeAddress;
            this.productAddress = productAddress;
            this.reviewAddress = reviewAddress;
            this.recommendationAddress = recommendationAddress;
        }
        public ServiceAddress(){

        }

        public String getCompositeAddress() {
            return compositeAddress;
        }

        public void setCompositeAddress(String compositeAddress) {this.compositeAddress = compositeAddress;
        }

        public String getProductAddress() {
            return productAddress;
        }

        public void setProductAddress(String productAddress) {
            this.productAddress = productAddress;
        }

        public String getReviewAddress() {
            return reviewAddress;
        }

        public void setReviewAddress(String reviewAddress) {
            this.reviewAddress = reviewAddress;
        }

        public String getRecommendationAddress() {
            return recommendationAddress;
        }

        public void setRecommendationAddress(String recommendationAddress) {
            this.recommendationAddress = recommendationAddress;
        }
}
