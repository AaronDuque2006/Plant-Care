import axios from "axios";

// const API_URL = "http://localhost:8080/PlantCare"; // Tu URL base del Backend
const API_URL = import.meta.env.VITE_API_URL

const api = axios.create({
    baseURL: API_URL,
});

// Interceptor: Antes de cada petición, revisa si hay token y pégalo
api.interceptors.request.use(
    (config) => {
    const token = localStorage.getItem("token"); // Buscamos el token en la "caja fuerte" del navegador
    if (token) {
        config.headers["Authorization"] = "Bearer " + token;
    }
    return config;
    },
    (error) => {
    return Promise.reject(error);
    }
);

export default api;
