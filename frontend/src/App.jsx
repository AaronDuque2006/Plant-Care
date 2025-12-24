import { Routes, Route } from 'react-router-dom';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import AddPlant from './pages/AddPlant';
import AdminDashboard from './pages/AdminDashboard';
import PlantDetails from './pages/PlantDetails';
import ThemeToggle from './components/ThemeToggle';

function App() {
  return (
    <div className="d-flex flex-column min-vh-100">
      <ThemeToggle />
      <div className="container mt-3 flex-grow-1">
        <Routes>
          <Route path="/" element={<Login />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/add-plant" element={<AddPlant />} />
          <Route path="/plant/:id" element={<PlantDetails />} />
          <Route path="/admin" element={<AdminDashboard />} />
        </Routes>
      </div>
      
      <footer className="bg-light text-center py-3 mt-4 border-top">
        <p className="mb-0 text-muted">
          © {new Date().getFullYear()} Planta Care App | Desarrollado por <strong>Aaron Duque</strong>
        </p>
      </footer>
    </div>
  );
}

export default App;