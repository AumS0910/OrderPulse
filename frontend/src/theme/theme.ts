import { alpha, createTheme } from "@mui/material/styles";

const baseTheme = createTheme();

const theme = createTheme({
  palette: {
    mode: "light",
    primary: {
      main: "#0071E3",
      light: "#2997FF",
      dark: "#005BB5",
      contrastText: "#FFFFFF",
    },
    secondary: {
      main: "#6E6E73",
      light: "#8E8E93",
      dark: "#48484A",
      contrastText: "#FFFFFF",
    },
    background: {
      default: "#F5F5F7",
      paper: "#FFFFFF",
    },
    text: {
      primary: "#1D1D1F",
      secondary: "#6E6E73",
    },
    success: {
      main: "#34C759",
      light: "#4CD964",
      dark: "#248A3D",
    },
    warning: {
      main: "#FF9F0A",
      light: "#FFB340",
      dark: "#C77700",
    },
    error: {
      main: "#FF3B30",
      light: "#FF6259",
      dark: "#C6281E",
    },
    info: {
      main: "#0A84FF",
      light: "#5AC8FA",
      dark: "#0067CC",
    },
    divider: "#E5E5EA",
  },
  shape: {
    borderRadius: 4,
  },
  typography: {
    fontFamily: '"SF Pro Text", "SF Pro Display", -apple-system, BlinkMacSystemFont, "Segoe UI", Helvetica, Arial, sans-serif',
    h1: {
      fontWeight: 600,
      letterSpacing: "-0.02em",
    },
    h2: {
      fontWeight: 600,
      letterSpacing: "-0.02em",
    },
    h3: {
      fontWeight: 600,
      letterSpacing: "-0.01em",
    },
    h4: {
      fontWeight: 600,
      letterSpacing: "-0.01em",
    },
    h5: {
      fontWeight: 600,
      letterSpacing: "-0.01em",
    },
    h6: {
      fontWeight: 600,
      letterSpacing: "0",
    },
    overline: {
      letterSpacing: "0.08em",
      fontWeight: 500,
    },
    button: {
      textTransform: "none",
      fontWeight: 600,
      letterSpacing: "0",
    },
  },
  components: {
    MuiCssBaseline: {
      styleOverrides: {
        body: {
          backgroundColor: "#F5F5F7",
          color: "#1D1D1F",
        },
      },
    },
    MuiPaper: {
      styleOverrides: {
        root: {
          backgroundImage: "none",
          backgroundColor: "#FFFFFF",
          border: "1px solid #E9E9ED",
          boxShadow: "0 6px 18px rgba(0,0,0,0.05)",
        },
      },
    },
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: 10,
          transition: "transform 0.15s ease, box-shadow 0.2s ease, background 0.2s ease",
          "&:active": {
            transform: "scale(0.98)",
          },
        },
        contained: {
          background: "#0071E3",
          boxShadow: "0 6px 18px rgba(0,113,227,0.25)",
          "&:hover": {
            background: "#0062C7",
            boxShadow: "0 8px 22px rgba(0,113,227,0.3)",
          },
        },
        outlined: {
          borderColor: "#D2D2D7",
          color: "#1D1D1F",
          "&:hover": {
            borderColor: "#B8B8BE",
            backgroundColor: alpha("#1D1D1F", 0.03),
          },
        },
      },
    },
    MuiTextField: {
      styleOverrides: {
        root: {
          "& .MuiOutlinedInput-root": {
            borderRadius: 10,
            backgroundColor: "#FFFFFF",
            "& .MuiOutlinedInput-notchedOutline": {
              borderColor: "#D2D2D7",
            },
            "&:hover .MuiOutlinedInput-notchedOutline": {
              borderColor: "#B8B8BE",
            },
            "&.Mui-focused .MuiOutlinedInput-notchedOutline": {
              borderColor: "#0071E3",
              boxShadow: `0 0 0 3px ${alpha("#0071E3", 0.15)}`,
            },
          },
          "& .MuiInputLabel-root": {
            color: "#6E6E73",
          },
        },
      },
    },
    MuiTableCell: {
      styleOverrides: {
        root: {
          borderBottom: "1px solid #EFEFF4",
        },
        head: {
          color: "#6E6E73",
          fontWeight: 500,
        },
      },
    },
    MuiSkeleton: {
      styleOverrides: {
        root: {
          backgroundColor: "#E9E9ED",
        },
      },
    },
    MuiTooltip: {
      styleOverrides: {
        tooltip: {
          backgroundColor: "#1D1D1F",
          border: "1px solid #2F2F32",
        },
      },
    },
    MuiDialog: {
      styleOverrides: {
        paper: {
          borderRadius: 12,
        },
      },
    },
  },
});

theme.components = {
  ...theme.components,
  MuiContainer: {
    styleOverrides: {
      root: {
        [baseTheme.breakpoints.down("sm")]: {
          paddingLeft: 16,
          paddingRight: 16,
        },
      },
    },
  },
};

export default theme;
