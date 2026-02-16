import React, { useCallback, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { motion } from "framer-motion";
import {
  Alert,
  Box,
  Button,
  Chip,
  Divider,
  Grid,
  LinearProgress,
  Paper,
  Skeleton,
  Stack,
  Typography,
  alpha,
} from "@mui/material";
import ShoppingBagRoundedIcon from "@mui/icons-material/ShoppingBagRounded";
import MonetizationOnRoundedIcon from "@mui/icons-material/MonetizationOnRounded";
import QueryStatsRoundedIcon from "@mui/icons-material/QueryStatsRounded";
import AutorenewRoundedIcon from "@mui/icons-material/AutorenewRounded";
import ArrowForwardRoundedIcon from "@mui/icons-material/ArrowForwardRounded";
import TimelineRoundedIcon from "@mui/icons-material/TimelineRounded";
import AppShell from "../components/layout/AppShell";
import { orderService } from "../services/orderService";
import { OrderAnalytics, OrderResponse, OrderStatus } from "../types/api";
import { toErrorMessage } from "../utils/error";
import { useAuth } from "../hooks/useAuth";
import { useWebSocket } from "../hooks/useWebSocket";

type ActivityEvent = {
  id: number;
  customerName: string;
  status: OrderStatus;
  createdAt: string;
};

const statusColorMap: Record<OrderStatus, { bg: string; text: string }> = {
  PENDING: { bg: "rgba(255,159,10,0.14)", text: "#9A6800" },
  CONFIRMED: { bg: "rgba(0,113,227,0.14)", text: "#005BB5" },
  PROCESSING: { bg: "rgba(142,142,147,0.16)", text: "#4A4A4F" },
  SHIPPED: { bg: "rgba(10,132,255,0.14)", text: "#0059A8" },
  DELIVERED: { bg: "rgba(52,199,89,0.14)", text: "#1F7A39" },
  CANCELLED: { bg: "rgba(255,59,48,0.14)", text: "#A3261E" },
};

const statusOrder: OrderStatus[] = [
  "PENDING",
  "CONFIRMED",
  "PROCESSING",
  "SHIPPED",
  "DELIVERED",
  "CANCELLED",
];

const Dashboard: React.FC = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [loading, setLoading] = useState<boolean>(true);
  const [errorMessage, setErrorMessage] = useState<string>("");
  const [analytics, setAnalytics] = useState<OrderAnalytics | null>(null);
  const [recentOrders, setRecentOrders] = useState<OrderResponse[]>([]);
  const [activityFeed, setActivityFeed] = useState<ActivityEvent[]>([]);

  const loadOverview = useCallback(async () => {
    setLoading(true);
    setErrorMessage("");
    try {
      const [analyticsData, recentPage] = await Promise.all([
        orderService.getOrderAnalytics(),
        orderService.listOrders({ page: 0, size: 6 }),
      ]);
      setAnalytics(analyticsData);
      setRecentOrders(recentPage.content);
    } catch (error) {
      setErrorMessage(toErrorMessage(error));
    } finally {
      setLoading(false);
    }
  }, []);

  React.useEffect(() => {
    loadOverview();
  }, [loadOverview]);

  const handleWebSocketOrderUpdate = useCallback(
    (order: OrderResponse) => {
      setRecentOrders((prev) => {
        const withoutCurrent = prev.filter((item) => item.id !== order.id);
        return [order, ...withoutCurrent].slice(0, 6);
      });
      setActivityFeed((prev) => [
        {
          id: order.id,
          customerName: order.customerName,
          status: order.status,
          createdAt: new Date().toISOString(),
        },
        ...prev,
      ].slice(0, 8));
      loadOverview();
    },
    [loadOverview]
  );

  const { connected: liveUpdatesConnected } = useWebSocket(handleWebSocketOrderUpdate);

  const totalOrders = analytics?.totalOrders ?? 0;
  const totalRevenue = analytics?.totalRevenue ?? 0;
  const averageOrderValue = analytics?.averageOrderValue ?? 0;
  const deliveredCount = analytics?.ordersByStatus?.DELIVERED ?? 0;
  const fulfillmentRate = totalOrders > 0 ? Math.round((deliveredCount / totalOrders) * 100) : 0;

  const headlineStats = [
    {
      label: "Total Orders",
      value: totalOrders,
      icon: <ShoppingBagRoundedIcon />,
      tone: "#0071E3",
      helper: "All-time orders processed",
    },
    {
      label: "Total Revenue",
      value: `$${Number(totalRevenue).toFixed(2)}`,
      icon: <MonetizationOnRoundedIcon />,
      tone: "#34C759",
      helper: "Across all successful orders",
    },
    {
      label: "Avg Order Value",
      value: `$${Number(averageOrderValue).toFixed(2)}`,
      icon: <QueryStatsRoundedIcon />,
      tone: "#8E8E93",
      helper: "Per order average",
    },
    {
      label: "Fulfillment Rate",
      value: `${fulfillmentRate}%`,
      icon: <TimelineRoundedIcon />,
      tone: "#0A84FF",
      helper: "Delivered vs total orders",
    },
  ];

  const statusRows = useMemo(
    () =>
      statusOrder.map((status) => {
        const count = analytics?.ordersByStatus?.[status] ?? 0;
        const ratio = totalOrders ? (count / totalOrders) * 100 : 0;
        return { status, count, ratio };
      }),
    [analytics, totalOrders]
  );

  return (
    <AppShell>
      <Stack spacing={2.2}>
        <Paper
          className="glass-card"
          sx={{
            p: { xs: 2.2, md: 3 },
            borderRadius: 3,
            background: "linear-gradient(180deg, #FFFFFF 0%, #FAFCFF 100%)",
          }}
        >
          <Stack direction={{ xs: "column", md: "row" }} spacing={2} justifyContent="space-between" alignItems={{ xs: "flex-start", md: "center" }}>
            <Box>
              <Typography variant="overline" color="text.secondary">
                Executive Dashboard
              </Typography>
              <Typography variant="h4" sx={{ mt: 0.8 }}>
                Welcome back, {user?.firstName}
              </Typography>
              <Typography variant="body1" color="text.secondary" sx={{ mt: 0.8 }}>
                View business health at a glance and jump into order operations.
              </Typography>
            </Box>
            <Stack direction="row" spacing={1.2}>
              <Button
                variant="outlined"
                startIcon={<AutorenewRoundedIcon />}
                onClick={loadOverview}
              >
                Refresh
              </Button>
              <Button
                variant="contained"
                endIcon={<ArrowForwardRoundedIcon />}
                className="shine-button"
                onClick={() => navigate("/orders")}
              >
                Open Orders
              </Button>
            </Stack>
          </Stack>
        </Paper>

        <Alert severity={liveUpdatesConnected ? "success" : "warning"}>
          Realtime stream: {liveUpdatesConnected ? "connected" : "disconnected"}
        </Alert>

        {errorMessage ? <Alert severity="error">{errorMessage}</Alert> : null}

        <Grid container spacing={1.8}>
          {headlineStats.map((card, index) => (
            <Grid key={card.label} size={{ xs: 12, sm: 6, xl: 3 }}>
              <motion.div
                initial={{ opacity: 0, y: 14 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.24, delay: index * 0.06 }}
              >
                <Paper className="glass-card" sx={{ p: 2, borderRadius: 3 }}>
                  <Stack direction="row" justifyContent="space-between" alignItems="center">
                    <Box>
                      <Typography variant="caption" color="text.secondary">
                        {card.label}
                      </Typography>
                      <Typography variant="h5" sx={{ mt: 0.4, fontWeight: 700 }}>
                        {loading ? <Skeleton width={120} animation="wave" /> : card.value}
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        {card.helper}
                      </Typography>
                    </Box>
                    <Box
                      sx={{
                        width: 40,
                        height: 40,
                        borderRadius: 2.5,
                        display: "grid",
                        placeItems: "center",
                        bgcolor: alpha(card.tone, 0.13),
                        color: card.tone,
                      }}
                    >
                      {card.icon}
                    </Box>
                  </Stack>
                </Paper>
              </motion.div>
            </Grid>
          ))}
        </Grid>

        <Grid container spacing={1.8}>
          <Grid size={{ xs: 12, lg: 6 }}>
            <Paper className="glass-card" sx={{ p: 2.2, borderRadius: 3, height: "100%" }}>
              <Typography variant="subtitle1" sx={{ fontWeight: 700 }}>
                Order Status Distribution
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 1.6 }}>
                Breakdown of all orders by lifecycle stage.
              </Typography>
              <Stack spacing={1.3}>
                {statusRows.map((row) => (
                  <Box key={row.status}>
                    <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 0.5 }}>
                      <Typography variant="body2">{row.status}</Typography>
                      <Typography variant="caption" color="text.secondary">
                        {row.count} ({row.ratio.toFixed(0)}%)
                      </Typography>
                    </Stack>
                    <LinearProgress
                      variant="determinate"
                      value={row.ratio}
                      sx={{
                        height: 8,
                        borderRadius: 999,
                        backgroundColor: "#ECEFF4",
                        "& .MuiLinearProgress-bar": {
                          borderRadius: 999,
                          backgroundColor: "#0071E3",
                        },
                      }}
                    />
                  </Box>
                ))}
              </Stack>
            </Paper>
          </Grid>

          <Grid size={{ xs: 12, lg: 6 }}>
            <Paper className="glass-card" sx={{ p: 2.2, borderRadius: 3, height: "100%" }}>
              <Typography variant="subtitle1" sx={{ fontWeight: 700 }}>
                Live Activity Feed
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 1.6 }}>
                Latest realtime updates from websocket and order events.
              </Typography>
              <Stack spacing={1.2}>
                {(activityFeed.length ? activityFeed : recentOrders.slice(0, 5)).map((event) => (
                  <Stack
                    key={`${event.id}-${event.createdAt}`}
                    direction="row"
                    justifyContent="space-between"
                    alignItems="center"
                    sx={{
                      px: 1.2,
                      py: 1,
                      borderRadius: 2,
                      bgcolor: "#FAFBFD",
                      border: "1px solid #ECEFF4",
                    }}
                  >
                    <Box>
                      <Typography variant="body2" sx={{ fontWeight: 600 }}>
                        #{event.id} {event.customerName}
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        {new Date(event.createdAt).toLocaleString()}
                      </Typography>
                    </Box>
                    <Chip
                      size="small"
                      label={event.status}
                      sx={{
                        bgcolor: statusColorMap[event.status].bg,
                        color: statusColorMap[event.status].text,
                        fontWeight: 600,
                      }}
                    />
                  </Stack>
                ))}
                {!loading && activityFeed.length === 0 && recentOrders.length === 0 ? (
                  <Typography variant="body2" color="text.secondary">
                    No activity yet. Create your first order from the Orders workspace.
                  </Typography>
                ) : null}
              </Stack>
            </Paper>
          </Grid>
        </Grid>

        <Paper className="glass-card" sx={{ borderRadius: 3, overflow: "hidden" }}>
          <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ px: 2.2, py: 1.8 }}>
            <Typography variant="subtitle1" sx={{ fontWeight: 700 }}>
              Recent Orders
            </Typography>
            <Button size="small" onClick={() => navigate("/orders")} endIcon={<ArrowForwardRoundedIcon />}>
              View all
            </Button>
          </Stack>
          <Divider />
          <Stack>
            {loading
              ? new Array(5)
                  .fill(0)
                  .map((_, index) => <Skeleton key={`recent-skeleton-${index}`} variant="rounded" height={44} sx={{ m: 1 }} />)
              : recentOrders.map((row, index) => (
                  <Stack
                    key={row.id}
                    direction="row"
                    justifyContent="space-between"
                    alignItems="center"
                    sx={{
                      px: 2.2,
                      py: 1.2,
                      borderBottom: index === recentOrders.length - 1 ? "none" : "1px solid #F0F1F5",
                    }}
                  >
                    <Box>
                      <Typography variant="body2" sx={{ fontWeight: 600 }}>
                        #{row.id} {row.customerName}
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        {row.productDescription}
                      </Typography>
                    </Box>
                    <Stack direction="row" spacing={1.5} alignItems="center">
                      <Typography variant="body2" sx={{ fontWeight: 600 }}>
                        ${row.totalPrice.toFixed(2)}
                      </Typography>
                      <Chip
                        size="small"
                        label={row.status}
                        sx={{
                          bgcolor: statusColorMap[row.status].bg,
                          color: statusColorMap[row.status].text,
                          fontWeight: 600,
                        }}
                      />
                    </Stack>
                  </Stack>
                ))}
            {!loading && recentOrders.length === 0 ? (
              <Box sx={{ p: 2.2 }}>
                <Typography variant="body2" color="text.secondary">
                  No recent orders available.
                </Typography>
              </Box>
            ) : null}
          </Stack>
        </Paper>
      </Stack>
    </AppShell>
  );
};

export default Dashboard;
