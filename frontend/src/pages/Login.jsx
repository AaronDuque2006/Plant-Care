import React, { useState } from "react";
import AuthService from "../services/auth.service";
import { Link, useNavigate } from "react-router-dom";

const Login = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState("");

  const navigate = useNavigate(); // Hook para redirigir a otra página

    const handleLogin = (e) => {
    e.preventDefault(); // Evita que la página se recargue
    setMessage("");
    setLoading(true);

    AuthService.login(username, password).then(
        () => {
        // Si todo sale bien, vamos al Dashboard (que crearemos luego)
        navigate("/dashboard");
        window.location.reload(); // Recarga para aplicar cambios en navbar
        },
        (error) => {
        // Si falla, mostramos el error que viene del backend
        const resMessage =
            (error.response &&
            error.response.data &&
            error.response.data.message) ||
            error.message ||
            error.toString();

        setLoading(false);
        setMessage(resMessage);
        }
    );
    };

    return (
    <div className="col-md-12">
        <div className="card card-container p-4 mt-5 mx-auto" style={{ maxWidth: "400px" }}>
        <h2 className="text-center text-success mb-4">PlantCare 🌿</h2>
        
        <form onSubmit={handleLogin}>
            <div className="form-group mb-3">
            <label htmlFor="username">Usuario</label>
            <input
                type="text"
                className="form-control"
                name="username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
            />
            </div>

            <div className="form-group mb-3">
            <label htmlFor="password">Contraseña</label>
            <input
                type="password"
                className="form-control"
                name="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
            />
            </div>

            <div className="form-group d-grid gap-2">
            <button className="btn btn-success btn-block" disabled={loading}>
                {loading && (
                <span className="spinner-border spinner-border-sm me-2"></span>
                )}
                <span>Iniciar Sesión</span>
            </button>
            </div>

            <div className="mt-3 text-center">
                <p>¿Nuevo aquí? <Link to="/register">Crea una cuenta</Link></p>
            </div>

            {message && (
            <div className="form-group mt-3">
                <div className="alert alert-danger" role="alert">
                {message}
                </div>
            </div>
            )}
        </form>
        </div>
    </div>
    );
};

export default Login;