import React from "react";
import { Navigate, Route, Routes, useLocation } from "react-router-dom";
import { Box, Skeleton, Stack } from "@mui/material";
import { AnimatePresence, motion } from "framer-motion";
import { useAuth } from "./hooks/useAuth";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Dashboard from "./pages/Dashboard";
import Orders from "./pages/Orders";

interface GuardProps {
  children: React.ReactNode;
}

const pageTransition = {
  initial: { opacity: 0, y: 24 },
  animate: { opacity: 1, y: 0 },
  exit: { opacity: 0, y: -16 },
  transition: { duration: 0.32, ease: "easeOut" as const },
};

const FullScreenLoader: React.FC = () => (
  <Box
    className="mesh-bg"
    sx={{
      minHeight: "100vh",
      display: "grid",
      placeItems: "center",
    }}
  >
    <Box className="content-layer glass-card" sx={{ width: 360, p: 3, borderRadius: 3 }}>
      <Stack spacing={1.25}>
        <Skeleton variant="text" width="42%" height={34} animation="wave" />
        <Skeleton variant="rounded" width="100%" height={52} animation="wave" />
        <Skeleton variant="rounded" width="100%" height={52} animation="wave" />
        <Skeleton variant="rounded" width="68%" height={44} animation="wave" />
      </Stack>
    </Box>
  </Box>
);

const ProtectedRoute: React.FC<GuardProps> = ({ children }) => {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return <FullScreenLoader />;
  }

  return isAuthenticated ? <>{children}</> : <Navigate to="/login" replace />;
};

const PublicRoute: React.FC<GuardProps> = ({ children }) => {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return <FullScreenLoader />;
  }

  return isAuthenticated ? <Navigate to="/dashboard" replace /> : <>{children}</>;
};

const AnimatedRoutes: React.FC = () => {
  const location = useLocation();

  return (
    <AnimatePresence mode="wait">
      <motion.div key={location.pathname} {...pageTransition}>
        <Routes location={location}>
          <Route
            path="/login"
            element={
              <PublicRoute>
                <Login />
              </PublicRoute>
            }
          />
          <Route
            path="/register"
            element={
              <PublicRoute>
                <Register />
              </PublicRoute>
            }
          />
          <Route
            path="/dashboard"
            element={
              <ProtectedRoute>
                <Dashboard />
              </ProtectedRoute>
            }
          />
          <Route
            path="/orders"
            element={
              <ProtectedRoute>
                <Orders />
              </ProtectedRoute>
            }
          />
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </motion.div>
    </AnimatePresence>
  );
};

const App: React.FC = () => <AnimatedRoutes />;

export default App;
