import axios from "axios";

const api = axios.create({

    baseURL: "http://localhost:8082",

    withCredentials: true

});

// Her istekten önce Access Token'ı Header'a ekler.
api.interceptors.request.use(

    (config) => {

        // Tarayıcıdaki Access Token'ı alır.
        const token = localStorage.getItem("accessToken");

        // Token varsa Authorization Header'ına ekler.
        if (token) {

            config.headers.Authorization = `Bearer ${token}`;

        }

        return config;

    },

    (error) => {

        return Promise.reject(error);

    }

);

export default api;