import './App.css';
import LogIn from './components/LogIn';
import AdminHomePage from './components/admin/AdminHomePage';
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import Certificates from './components/admin/Certificates';
import { ToastProvider } from "react-toast-notifications";

function App() {
  return (
    <ToastProvider autoDismiss={true}>
      <Router>
        <div className="App">
          <Routes>
            <Route exact path="/" element={<LogIn/>} />
            <Route exact path="/admin" element={<AdminHomePage/>} />
            <Route exact path="/certificates" element={<Certificates/>} />
          </Routes>
        </div>
      </Router>
    </ToastProvider>
  );
}

export default App;
