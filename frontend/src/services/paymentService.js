import axiosInstance from "../api/axios";

const CREATE_ORDER_URL = "/api/payments/create-order";
const VERIFY_PAYMENT_URL = "/api/payments/verify";

export const createPaymentOrder = async (planId) => {
  try {
    const response = await axiosInstance.post(CREATE_ORDER_URL, {
      planId,
    });

    return response.data;
  } catch (error) {
    const message =
      error.response?.data?.message ||
      "Unable to create payment order.";

    throw new Error(message);
  }
};

export const verifyPayment = async (paymentDetails) => {
  try {
    const response = await axiosInstance.post(
      VERIFY_PAYMENT_URL,
      paymentDetails
    );

    return response.data;
  } catch (error) {
    const message =
      error.response?.data?.message ||
      "Payment verification failed.";

    throw new Error(message);
  }
};