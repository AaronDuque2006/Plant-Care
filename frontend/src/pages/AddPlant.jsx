import React, { useState, useEffect } from "react";
import PlantService from "../services/plant.service";
import { useNavigate } from "react-router-dom";

const AddPlant = () => {
    // Estado para el formulario
    const [nickname, setNickname] = useState("");
    const [speciesId, setSpeciesId] = useState("");
    const [acquisitionDate, setAcquisitionDate] = useState("");
    const [lastWateringDate, setLastWateringDate] = useState("");
    
    // Estado para la foto
    const [selectedFile, setSelectedFile] = useState(null);

    // Estado para la lista de especies (Dropdown)
    const [speciesList, setSpeciesList] = useState([]);

    const navigate = useNavigate();

    // Al cargar la página, traemos las especies del Backend
    useEffect(() => {
        PlantService.getAllSpecies().then(
            (response) => {
                setSpeciesList(response.data);
                // Si hay especies, seleccionamos la primera por defecto
                if (response.data.length > 0) {
                    setSpeciesId(response.data[0].id);
                }
            },
            (error) => {
                console.error("Error cargando especies", error);
            }
        );
    }, []);

    const handleFileChange = (e) => {
        if (e.target.files && e.target.files[0]) {
            setSelectedFile(e.target.files[0]);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        // 1. Armamos el objeto de la planta
        const newPlant = {
            nickname,
            speciesId: parseInt(speciesId),
            acquisitionDate,
            lastWateringDate,
        };

        try {
            // 2. Primero creamos la planta (JSON)
            const response = await PlantService.createPlant(newPlant);
            const createdPlantId = response.data.id;

            // 3. Si hay foto seleccionada, la subimos usando el ID recién creado
            if (selectedFile) {
                try {
                    await PlantService.uploadPlantImage(createdPlantId, selectedFile);
                } catch (uploadError) {
                    console.error("Error subiendo imagen:", uploadError);
                    alert("Planta creada, pero hubo un error subiendo la imagen: " + (uploadError.response?.data?.message || uploadError.message));
                    // Aún así navegamos, porque la planta sí se creó
                }
            }

            alert("¡Planta agregada exitosamente! 🌱");
            navigate("/dashboard");

        } catch (error) {
            console.error("Error creando planta:", error);
            alert("Ocurrió un error al crear la planta: " + (error.response?.data?.message || error.message));
        }
    };

    return (
        <div className="container mt-5" style={{ maxWidth: "500px" }}>
            <div className="card shadow">
                <div className="card-header bg-success text-white">
                    <h4 className="mb-0">Nueva Planta 🌿</h4>
                </div>
                <div className="card-body">
                    <form onSubmit={handleSubmit}>
                        {/* 1. Nombre */}
                        <div className="mb-3">
                            <label className="form-label">Apodo de la planta</label>
                            <input
                                type="text"
                                className="form-control"
                                placeholder="Ej: Pepito el Cactus"
                                value={nickname}
                                onChange={(e) => setNickname(e.target.value)}
                                required
                            />
                        </div>

                        {/* 2. Selector de Especie */}
                        <div className="mb-3">
                            <label className="form-label">Tipo de Planta (Especie)</label>
                            <select
                                className="form-select"
                                value={speciesId}
                                onChange={(e) => setSpeciesId(e.target.value)}
                                required
                            >
                                {speciesList.map((species) => (
                                    <option key={species.id} value={species.id}>
                                        {species.commonName} (Riego cada {species.wateringFrequencyDays} días)
                                    </option>
                                ))}
                            </select>
                            {speciesList.length === 0 && (
                                <small className="text-danger">No hay especies cargadas en el sistema.</small>
                            )}
                        </div>

                        {/* 3. Fechas */}
                        <div className="mb-3">
                            <label className="form-label">¿Cuándo la compraste?</label>
                            <input
                                type="date"
                                className="form-control"
                                value={acquisitionDate}
                                onChange={(e) => setAcquisitionDate(e.target.value)}
                                required
                            />
                        </div>

                        <div className="mb-3">
                            <label className="form-label">¿Cuándo la regaste por última vez?</label>
                            <input
                                type="date"
                                className="form-control"
                                value={lastWateringDate}
                                onChange={(e) => setLastWateringDate(e.target.value)}
                                required
                            />
                        </div>

                        {/* 4. Foto (Opcional) */}
                        <div className="mb-3">
                            <label className="form-label">Foto de Portada (Opcional)</label>
                            <input 
                                type="file" 
                                className="form-control" 
                                accept="image/*"
                                onChange={handleFileChange}
                            />
                        </div>

                        <div className="d-grid gap-2">
                            <button type="submit" className="btn btn-success">
                                Guardar Planta
                            </button>
                            <button
                                type="button"
                                className="btn btn-secondary"
                                onClick={() => navigate("/dashboard")}
                            >
                                Cancelar
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default AddPlant;