import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import PlantService from "../services/plant.service";

const PlantDetails = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [plant, setPlant] = useState(null); 
    const [logs, setLogs] = useState([]);
    const [loading, setLoading] = useState(true);
    
    // Estados para nueva bitácora
    const [actionType, setActionType] = useState("WATER");
    const [notes, setNotes] = useState("");

    useEffect(() => {
        loadData();
    }, [id]);

    const loadData = async () => {
        setLoading(true);
        try {
            const plantRes = await PlantService.getPlantById(id);
            setPlant(plantRes.data);
            
            const logsRes = await PlantService.getPlantLogs(id);
            setLogs(logsRes.data);
        } catch (error) {
            console.error("Error cargando datos:", error);
        } finally {
            setLoading(false);
        }
    };

    const loadLogs = () => {
        PlantService.getPlantLogs(id).then(res => setLogs(res.data));
    };

    // Manejar selección de archivo
    const handleImageChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            PlantService.uploadPlantImage(id, file).then(
                (response) => {
                    // Actualizar la vista con la nueva foto que viene del backend
                    setPlant(prev => ({ ...prev, imageUrl: response.data.imageUrl }));
                    alert("¡Foto actualizada! 📸");
                },
                (error) => alert("Error subiendo imagen: " + (error.response?.data?.message || error.message))
            );
        }
    };

    const handleAddLog = (e) => {
        e.preventDefault();
        const newLog = { actionType, notes };
        
        PlantService.addPlantLog(id, newLog).then(
            () => {
                alert("Evento registrado");
                setNotes("");
                loadLogs(); // Recargar lista
                // Si fue riego, recargamos la planta para ver nueva fecha de riego
                if(actionType === "WATER") {
                    PlantService.getPlantById(id).then(res => setPlant(res.data));
                }
            },
            (err) => alert("Error: " + err.message)
        );
    };

    if (loading) return <div className="container mt-5 text-center">Cargando...</div>;
    if (!plant) return <div className="container mt-5 text-center">No se encontró la planta.</div>;

    return (
        <div className="container mt-4">
            {/* CABECERA CON FOTO DE PLANTA */}
            <div className="card mb-4 border-0 shadow-sm overflow-hidden">
                <div className="row g-0">
                    <div className="col-md-4 text-center bg-light d-flex align-items-center justify-content-center" style={{ minHeight: "200px" }}>
                        {plant?.imageUrl ? (
                            <img src={plant.imageUrl} alt="Planta" className="img-fluid rounded-start" style={{ maxHeight: "300px", width: "100%", objectFit: "cover" }} />
                        ) : (
                            <div className="p-5 text-muted">🌿 Sin Foto</div>
                        )}
                    </div>
                    <div className="col-md-8">
                        <div className="card-body">
                            <div className="d-flex justify-content-between">
                                <h2 className="card-title text-success">{plant?.nickname}</h2>
                                <button className="btn btn-outline-secondary btn-sm" onClick={() => navigate("/dashboard")}>
                                    Volver
                                </button>
                            </div>
                            <p className="card-text text-muted">{plant?.speciesName}</p>
                            
                            <hr />
                            
                            <div className="row">
                                <div className="col-sm-6">
                                    <p><strong>Último Riego:</strong> {plant.lastWateringDate}</p>
                                    <p><strong>Próximo Riego:</strong> {plant.nextWateringDate}</p>
                                </div>
                                <div className="col-sm-6 text-end">
                                    <span className={`badge bg-${plant.wateringStatus === 'CRITICAL' ? 'danger' : plant.wateringStatus === 'WARNING' ? 'warning' : 'success'} p-2`}>
                                        {plant.wateringStatus}
                                    </span>
                                </div>
                            </div>

                            {/* BOTÓN PARA SUBIR FOTO */}
                            <div className="mt-3">
                                <label className="btn btn-outline-success btn-sm">
                                    📷 Cambiar Foto de Portada
                                    <input type="file" hidden onChange={handleImageChange} accept="image/*" />
                                </label>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* SECCIÓN DE BITÁCORA */}
            <div className="row">
                {/* Formulario para nuevo log */}
                <div className="col-md-4">
                    <div className="card shadow-sm mb-4">
                        <div className="card-header bg-success text-white">Registrar Cuidado</div>
                        <div className="card-body">
                            <form onSubmit={handleAddLog}>
                                <div className="mb-3">
                                    <label className="form-label">Acción</label>
                                    <select 
                                        className="form-select" 
                                        value={actionType}
                                        onChange={(e) => setActionType(e.target.value)}
                                    >
                                        <option value="WATER">Riego 💧</option>
                                        <option value="FERTILIZE">Fertilización 🧪</option>
                                        <option value="PRUNE">Poda ✂️</option>
                                        <option value="REPOT">Trasplante 🪴</option>
                                        <option value="OTHER">Otro 📝</option>
                                    </select>
                                </div>
                                <div className="mb-3">
                                    <label className="form-label">Notas</label>
                                    <textarea 
                                        className="form-control" 
                                        rows="3"
                                        value={notes}
                                        onChange={(e) => setNotes(e.target.value)}
                                        placeholder="Ej: Se ve muy sana hoy"
                                    ></textarea>
                                </div>
                                <button type="submit" className="btn btn-success w-100">Guardar Evento</button>
                            </form>
                        </div>
                    </div>
                </div>

                {/* Lista de logs */}
                <div className="col-md-8">
                    <div className="card shadow-sm">
                        <div className="card-header">Historial de Cuidados</div>
                        <ul className="list-group list-group-flush">
                            {logs.length === 0 ? (
                                <li className="list-group-item text-center p-4 text-muted">Aún no hay registros de cuidado.</li>
                            ) : (
                                logs.map((log) => (
                                    <li key={log.id} className="list-group-item d-flex justify-content-between align-items-center">
                                        <div>
                                            <span className="badge bg-info text-dark me-2">{log.actionType}</span>
                                            <small className="text-muted">{log.date}</small>
                                            <p className="mb-0 mt-1">{log.notes}</p>
                                        </div>
                                    </li>
                                ))
                            )}
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default PlantDetails;