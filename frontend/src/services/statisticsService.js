import axiosInstance from "../api/axios";

// Get statistics of a user
export const getUserStatistics = async (userId) => {
  const response = await axiosInstance.get(
    `/api/statistics/${userId}`
  );

  return response.data;
};