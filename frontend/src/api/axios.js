import axios from "axios";

const axiosInstance = axios.create({
    baseURL: "http://localhost:8080",
    timeout: 15000,
});

axiosInstance.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem("token");

        console.log("REQUEST URL:", config.url);
        console.log("TOKEN BEFORE REQUEST:", token);

        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }

        console.log(
            "AUTHORIZATION HEADER:",
            config.headers.Authorization
        );

        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

axiosInstance.interceptors.response.use(
    (response) => response,
    (error) => {
        console.error(
            "API ERROR:",
            error.config?.url,
            error.response?.status,
            error.response?.data
        );

        /*
         * Debugging ke time token automatically delete mat karo.
         * Profile API ke 401 ka original reason pehle find karna hai.
         */
        return Promise.reject(error);
    }
);

export default axiosInstance;