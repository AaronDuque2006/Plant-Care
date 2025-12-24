import React, { useState } from "react";
import AuthService from "../services/auth.service";
import { Link, useNavigate } from "react-router-dom";

const Register = () => {
    const [username, setUsername] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [successful, setSuccessful] = useState(false);
    const [message, setMessage] = useState("");
    const navigate = useNavigate();

    const handleRegister = (e) => {
        e.preventDefault();
        setMessage("");
        setSuccessful(false);

        AuthService.register(username, email, password).then(
            (response) => {
                setMessage(response.data?.message || "Registro exitoso");
                setSuccessful(true);
                setTimeout(() => navigate("/login"), 1200);
            },
            (error) => {
                const resMessage =
                    (error.response && error.response.data && error.response.data.message) ||
                    error.message ||
                    error.toString();
                setMessage(resMessage);
                setSuccessful(false);
            }
        );
    };

    return (
        <main className="col-md-12 d-flex justify-content-center">
            <section className="card card-container p-4 mt-5" style={{ maxWidth: "400px" }}>
                <header>
                    <h2 className="text-center text-success mb-4">Únete a PlantCare 🌱</h2>
                </header>

                <form onSubmit={handleRegister} aria-label="Registro">
                    {!successful && (
                        <fieldset>
                            <div className="form-group mb-3">
                                <label htmlFor="username">Usuario</label>
                                <input
                                    id="username"
                                    type="text"
                                    className="form-control"
                                    name="username"
                                    value={username}
                                    onChange={(e) => setUsername(e.target.value)}
                                    required
                                />
                            </div>

                            <div className="form-group mb-3">
                                <label htmlFor="email">Email</label>
                                <input
                                    id="email"
                                    type="email"
                                    className="form-control"
                                    name="email"
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    required
                                />
                            </div>

                            <div className="form-group mb-3">
                                <label htmlFor="password">Contraseña</label>
                                <input
                                    id="password"
                                    type="password"
                                    className="form-control"
                                    name="password"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    required
                                />
                            </div>

                            <div className="d-grid gap-2">
                                <button type="submit" className="btn btn-primary btn-block">
                                    Registrarse
                                </button>
                            </div>
                        </fieldset>
                    )}

                    {message && (
                        <div className="form-group mt-3">
                            <div className={successful ? "alert alert-success" : "alert alert-danger"} role="alert">
                                {message}
                            </div>
                        </div>
                    )}

                    <div className="mt-3 text-center">
                        <Link to="/login">¿Ya tienes cuenta? Inicia Sesión</Link>
                    </div>
                </form>
            </section>
        </main>
    );
};

export default Register;