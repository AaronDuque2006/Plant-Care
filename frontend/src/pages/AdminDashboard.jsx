import { useState, useEffect } from "react";
import AdminService from "../services/admin.service";
import AuthService from "../services/auth.service";
import api from "../services/api"; // Para crear especies
import { useNavigate } from "react-router-dom";

const AdminDashboard = () => {
    const [users, setUsers] = useState([]);
    const [commonName, setCommonName] = useState("");
    const [wateringDays, setWateringDays] = useState(7);
    const [sunlightNeeds, setSunlightNeeds] = useState("INDIRECT");

    const navigate = useNavigate();

    useEffect(() => {
        const currentUser = AuthService.getCurrentUser();

        if (!currentUser) {
             navigate("/login");
             return;
        }

        if (!currentUser.roles || !Array.isArray(currentUser.roles)) {
            navigate("/dashboard");
            return;
        }

        // Verificación robusta de roles (maneja ["ROLE_ADMIN"] y [{name: "ROLE_ADMIN"}])
        const roles = currentUser.roles.map(r => (typeof r === 'string' ? r : r?.name));
        
        if (!roles.includes("ROLE_ADMIN")) {
            console.log("Acceso denegado: No es admin");
            navigate("/dashboard");
            return;
        }

        // 2. Cargar usuarios
        AdminService.getAllUsers().then(
            (res) => {
                console.log("Admin API Response:", res.data); // DEBUG
                if (Array.isArray(res.data)) {
                    setUsers(res.data);
                } else if (res.data && Array.isArray(res.data.content)) {
                    // Handle Spring Boot Page implementation
                    setUsers(res.data.content);
                } else {
                    console.error("Data received is not an array:", res.data);
                    setUsers([]);
                }
            },
            (err) => console.error("Error cargando usuarios", err)
        );
    }, [navigate]);

    const handleCreateSpecies = (e) => {
        e.preventDefault();
        api
            .post("/species", {
                commonName,
                wateringFrequencyDays: wateringDays,
                sunlightNeeds, // Campo obligatorio
                careTips: "Sin cuidados específicos", // Valor por defecto para evitar null
                scientificName: commonName // Por defecto el mismo nombre
            })
            .then(() => {
                alert("¡Especie creada!");
                setCommonName("");
                setWateringDays(7);
                setSunlightNeeds("INDIRECT");
            })
            .catch((err) => alert("Error: " + (err.response?.data?.message || err.message)));
    };

    return (
        <div className="container mt-4">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2 className="text-danger mb-0">Panel de Administración 🛡️</h2>
                <button className="btn btn-outline-primary" onClick={() => navigate("/dashboard")}>
                    Volver al Jardín 🌻
                </button>
            </div>

            {/* Formulario Especies */}
            <div className="card mb-4 border-danger">
                <div className="card-header bg-danger text-white">Nueva Especie</div>
                <div className="card-body">
                    <form
                        onSubmit={handleCreateSpecies}
                        className="row g-3 align-items-end"
                    >
                        <div className="col-md-4">
                            <label className="form-label">Nombre Común</label>
                            <input
                                type="text"
                                className="form-control"
                                value={commonName}
                                onChange={(e) => setCommonName(e.target.value)}
                                placeholder="Ej: Rosal"
                                required
                            />
                        </div>
                        <div className="col-md-3">
                            <label className="form-label">Luz Necesaria</label>
                            <select 
                                className="form-select"
                                value={sunlightNeeds}
                                onChange={(e) => setSunlightNeeds(e.target.value)}
                            >
                                <option value="DIRECT">Directa ☀️</option>
                                <option value="INDIRECT">Indirecta 🌤️</option>
                                <option value="SHADE">Sombra ☁️</option>
                            </select>
                        </div>
                        <div className="col-md-3">
                            <label className="form-label">Frecuencia (Días)</label>
                            <input
                                type="number"
                                className="form-control"
                                value={wateringDays}
                                onChange={(e) => setWateringDays(e.target.value)}
                                required
                                min="1"
                            />
                        </div>
                        <div className="col-md-2">
                            <button className="btn btn-success w-100">Guardar</button>
                        </div>
                    </form>
                </div>
            </div>

            {/* Tabla Usuarios */}
            <h3>Usuarios Registrados</h3>
            <table className="table table-striped shadow-sm">
                <thead className="table-dark">
                    <tr>
                        <th>ID</th>
                        <th>Usuario</th>
                        <th>Email</th>
                        <th>Roles</th>
                    </tr>
                </thead>
                <tbody>
                    {users.map((u) => (
                        <tr key={u.id}>
                            <td>{u.id}</td>
                            <td>{u.username}</td>
                            <td>{u.email}</td>
                            <td>
                                {Array.isArray(u.roles) 
                                    ? u.roles.map((r) => (typeof r === "string" ? r : r?.name || "")).join(", ")
                                    : "N/A"}
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default AdminDashboard;