import React, { useState, useEffect } from "react";
import PlantService from "../services/plant.service";
import AuthService from "../services/auth.service";
import { useNavigate, Link } from "react-router-dom";

const Dashboard = () => {
    const [plants, setPlants] = useState([]);
    const [loading, setLoading] = useState(true);
    
    // Inicializar estado isAdmin leyendo directamente del AuthService al inicio
    const [isAdmin] = useState(() => {
        const user = AuthService.getCurrentUser();
        if (user && user.roles) {
            const roles = user.roles.map(r => (typeof r === 'string' ? r : r.name || r.authority));
            return roles.includes("ROLE_ADMIN");
        }
        return false;
    });

    const navigate = useNavigate();

  // Efecto: Se ejecuta al cargar la página
    useEffect(() => {
    const user = AuthService.getCurrentUser();

    if (!user) {
      // Si no hay usuario logueado, lo mandamos al Login
        navigate("/login");
        return;
    }

    // Traer las plantas del backend
    PlantService.getAllPrivatePlants()
        .then((response) => {
        setPlants(response.data);
        setLoading(false);
        })
        .catch((error) => {
        console.error("Error cargando plantas:", error);
        setLoading(false);
        if (error.response && error.response.status === 401) {
            AuthService.logout();
            navigate("/login");
        }
        });
    }, [navigate]);

    const handleDelete = (id, nickname) => {
        // 1. Preguntar confirmación (para evitar clics accidentales)
        if (window.confirm(`¿Estás seguro de eliminar a "${nickname}"? 😢`)) {
            PlantService.deletePlant(id).then(
                () => {
                    // 2. Si el backend borró OK, actualizamos la lista visualmente
                    // Filtramos el array para quitar la planta que tenga ese ID
                    setPlants(plants.filter((plant) => plant.id !== id));
                },
                (error) => {
                    alert("Error al eliminar: " + error.message);
                }
            );
        }
    };

  // Función auxiliar para elegir el color del borde según el estado
    const getStatusColor = (status) => {
    switch (status) {
        case "CRITICAL":
        return "danger"; // Rojo Bootstrap
        case "WARNING":
        return "warning"; // Amarillo Bootstrap
        default:
        return "success"; // Verde Bootstrap
    }
    };

    const handleLogout = () => {
    AuthService.logout();
    navigate("/login");
    };

    return (
    <div className="container mt-4">
        <div className="d-flex justify-content-between align-items-center mb-4">
        <h2>Mi Jardín 🌻</h2>
            <div>
                {isAdmin && (
                    <Link to="/admin" className="btn btn-danger me-2">
                        🛡️ Panel Admin
                    </Link>
                )}
                <Link to="/add-plant" className="btn btn-success me-2">
                    + Nueva Planta
                </Link>
                <button className="btn btn-outline-danger" onClick={handleLogout}>
                    Cerrar Sesión
                </button>
            </div>
    </div>

    {loading ? (
        <div className="text-center">
            <div className="spinner-border text-success" role="status"></div>
            <p>Regando los bits...</p>
        </div>
    ) : (
        <div className="row">
            {plants.length === 0 ? (
            <div className="col-12 text-center">
                <div className="alert alert-info">
                ¡Tu jardín está vacío! 🌱 Agrega tu primera planta.
                </div>
            </div>
        ) : (
            plants.map((plant) => (
                <div className="col-md-4 mb-4" key={plant.id}>
                <div
                    className={`card border-${getStatusColor(
                    plant.wateringStatus
                    )} h-100 shadow-sm`}
                >
                    <div className="card-body">
                        {/* 1. IMAGEN DE PORTADA */}
                        <div className="text-center mb-3" style={{ height: "200px", overflow: "hidden", backgroundColor: "#f8f9fa" }}>
                            {plant.imageUrl ? (
                                <img 
                                    src={plant.imageUrl} 
                                    alt={plant.nickname} 
                                    className="img-fluid" 
                                    style={{ width: "100%", height: "100%", objectFit: "cover" }} 
                                />
                            ) : (
                                <div className="d-flex align-items-center justify-content-center h-100 text-muted">
                                    <span>🌿 Sin Foto</span>
                                </div>
                            )}
                        </div>

                        <h5 className="card-title">{plant.nickname}</h5>
                        <h6 className="card-subtitle mb-2 text-muted">
                            {plant.speciesName}
                        </h6>

                        <hr />

                        <p className="card-text">
                            <strong>Próximo Riego:</strong> {plant.nextWateringDate}{" "}
                            <br />
                        {/* Lógica visual del semáforo */}
                            {plant.wateringStatus === "CRITICAL" && (
                            <span className="badge bg-danger">
                                ¡Atrasado {Math.abs(plant.daysUntilWatering)} días! 🩸
                            </span>
                            )}
                            {plant.wateringStatus === "WARNING" && (
                            <span className="badge bg-warning text-dark">
                                ¡Toca hoy! 💧
                            </span>
                            )}
                            {plant.wateringStatus === "OK" && (
                            <span className="badge bg-success">
                                Faltan {plant.daysUntilWatering} días 😌
                            </span>
                            )}
                        </p>
                    </div>

                    <div className="card-footer bg-transparent border-0 d-flex justify-content-between">
                        <Link to={`/plant/${plant.id}`} className="btn btn-sm btn-primary">
                            Ver Detalles
                        </Link>

                        {/* Botón Eliminar */}
                        <button 
                            className="btn btn-sm btn-outline-danger"
                            onClick={() => handleDelete(plant.id, plant.nickname)}
                        >
                            🗑️ Eliminar
                        </button>
                    </div>
                </div>
                </div>
            ))
            )}
        </div>
        )}
    </div>
    );
};

export default Dashboard;
