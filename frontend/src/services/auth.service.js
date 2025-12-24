import api from "./api";

// --- NUEVA FUNCIÓN ---
const register = (username, email, password) => {
    return api.post("/auth/signup", {
    username,
    email,
    password
    });
};
// ---------------------

const login = async (username, password) => {
    const response = await api
        .post("/auth/signin", {
            username,
            password,
        });
    if (response.data.token) {
        localStorage.setItem("user", JSON.stringify(response.data));
        localStorage.setItem("token", response.data.token);
    }
    return response.data;
};

const logout = () => {
    localStorage.removeItem("user");
    localStorage.removeItem("token");
};

const getCurrentUser = () => {
    return JSON.parse(localStorage.getItem("user"));
};

const AuthService = {
    register, // <--- ¡No olvides exportarla aquí!
    login,
    logout,
    getCurrentUser,
};

export default AuthService;