import api from "./api"; // Usamos nuestra instancia configurada con el Token

const getPlantLogs = (plantId) => {
    // Esto coincide con tu @GetMapping("/plant/{plantId}")
    return api.get(`/logs/plant/${plantId}`);
};

const addPlantLog = (plantId, logData) => {
    // Tu backend espera un CareLogRequest.
    // Metemos el plantId AQUÍ, porque el endpoint es POST /logs (sin ID en URL)
    const requestBody = {
        plantId: plantId, 
        actionType: logData.actionType,
        notes: logData.notes,
        photoUrl: logData.photoUrl
    };

    return api.post("/logs", requestBody);
};

const getPlantById = (id) => {
  return api.get(`/plants/${id}`);
};

const getAllPrivatePlants = () => {
  return api.get("/plants"); // Axios le pegará a http://localhost:8080/PlantCare/plants
};

const getAllSpecies = () => {
  return api.get("/species"); 
};

const createPlant = (plantData) => {
  return api.post("/plants", plantData);
};

const deletePlant = (id) => {
  return api.delete(`/plants/${id}`);
};

const uploadPlantImage = (plantId, file) => {
  let formData = new FormData();
  formData.append("file", file); // "file" debe coincidir con @RequestParam("file") del backend

  return api.post(`/plants/${plantId}/image`, formData);
};

const PlantService = {
    getPlantById,
    getAllPrivatePlants,
    getAllSpecies,
    createPlant,
    deletePlant,
    getPlantLogs,
    addPlantLog,
    uploadPlantImage,
};

export default PlantService;