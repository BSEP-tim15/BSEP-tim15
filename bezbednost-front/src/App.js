import './App.css';
import LogIn from './components/LogIn';
import AdminHomePage from './components/admin/AdminHomePage';
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import Certificates from './components/admin/Certificates';

function App() {
  return (
    <Router>
      <div className="App">
        <Routes>
          <Route exact path="/" element={<LogIn/>} />
          <Route exact path="/admin" element={<AdminHomePage/>} />
          <Route exact path="/certificates" element={<Certificates/>} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
