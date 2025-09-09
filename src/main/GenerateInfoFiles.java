package main;

import java.util.*;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Main class for registering vendors, managing product sales,
 * and displaying a formatted sales summary.
 */
public class GenerateInfoFiles {

    /**
     * Represents a product with ID, name, and unit price.
     */
    static class Producto {
        String id;
        String nombre;
        double precio;

        Producto(String id, String nombre, double precio) {
            this.id = id;
            this.nombre = nombre;
            this.precio = precio;
        }
    }

    /**
     * Represents a seller with name, ID, and a list of sales.
     */
    static class Vendedor {
        String nombre;
        long id;
        List<Venta> ventas = new ArrayList<>();

        Vendedor(String nombre, long id) {
            this.nombre = nombre;
            this.id = id;
        }

        /**
         * Adds a sale to the seller's record.
         */
        void agregarVenta(Producto producto, int cantidad) {
            ventas.add(new Venta(producto, cantidad));
        }

        /**
         * Displays a formatted sales summary in table format.
         */
        void mostrarResumen(NumberFormat formatoMoneda) {
            System.out.println("\n Sales Summary:");
            System.out.println(" Seller: " + nombre + " | ID: " + id);

            if (ventas.isEmpty()) {
                System.out.println("   (No sales recorded)");
                return;
            }

            // Table headers
            System.out.printf("%-20s %-10s %-15s %-15s%n",
                    "Product", "Code", "Quantity", "Total Value");
            System.out.println("---------------------------------------------------------------");

            double total = 0;
            int totalItems = 0;

            for (Venta venta : ventas) {
                double subtotal = venta.producto.precio * venta.cantidad;
                total += subtotal;
                totalItems += venta.cantidad;

                System.out.printf("%-20s %-10s %-15s %-15s%n",
                        venta.producto.nombre,
                        venta.producto.id,
                        venta.cantidad,
                        formatoMoneda.format(subtotal));
            }

            System.out.println("---------------------------------------------------------------");
            System.out.printf("Total sold: %d items | Total value: %s%n",
                    totalItems, formatoMoneda.format(total));
        }
    }

    /**
     * Represents a sale of a product with quantity.
     */
    static class Venta {
        Producto producto;
        int cantidad;

        Venta(Producto producto, int cantidad) {
            this.producto = producto;
            this.cantidad = cantidad;
        }
    }

    /**
     * Main execution method: handles seller registration, product listing,
     * sale recording, and summary display.
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        NumberFormat formatoColombiano = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));

        // Preloaded product catalog
        Map<String, Producto> productos = new LinkedHashMap<>();
        productos.put("P1", new Producto("P1", "Café", 8500));
        productos.put("P2", new Producto("P2", "Azúcar", 4200));
        productos.put("P3", new Producto("P3", "Arroz", 3900));
        productos.put("P4", new Producto("P4", "Aceite", 9800));
        productos.put("P5", new Producto("P5", "Sal", 2500));

        // Seller registration
        List<Vendedor> vendedores = new ArrayList<>();
        System.out.println("Seller Registration");

        boolean agregarVendedores = true;
        while (agregarVendedores) {
            System.out.print("Enter seller name: ");
            String nombre = scanner.nextLine().trim();

            System.out.print("Enter seller ID (long number): ");
            long id = Long.parseLong(scanner.nextLine().trim());

            vendedores.add(new Vendedor(nombre, id));

            System.out.print("Add another seller? (y/n): ");
            agregarVendedores = scanner.nextLine().trim().equalsIgnoreCase("y");
        }

        // Display registered Seller´s
        System.out.println("\n Registered Vendors:");
        for (Vendedor v : vendedores) {
            System.out.println("- " + v.nombre + " (" + v.id + ")");
        }

        // Seller selection with retry
        Vendedor vendedorSeleccionado = null;
        while (vendedorSeleccionado == null) {
            System.out.print("\nEnter seller name or ID to record sales: ");
            String entrada = scanner.nextLine().trim();

            for (Vendedor v : vendedores) {
                if (v.nombre.equalsIgnoreCase(entrada) || String.valueOf(v.id).equals(entrada)) {
                    vendedorSeleccionado = v;
                    break;
                }
            }

            if (vendedorSeleccionado == null) {
                System.out.println("✘ seller not found. Please try again.");
            }
        }

        // Display product catalog as table
        System.out.println("\n Available Products:");
        System.out.printf("%-10s %-20s %-20s%n", "Code", "Description", "Unit Price");
        System.out.println("------------------------------------------------------------------");

        for (Producto p : productos.values()) {
            String precioFormateado = formatoColombiano.format(p.precio);
            System.out.printf("%-10s %-20s %-20s%n", p.id, p.nombre, precioFormateado);
        }

        // Record sales
        boolean agregarVentas = true;
        while (agregarVentas) {
            System.out.print("\nEnter product ID: ");
            String productoId = scanner.nextLine().trim().toUpperCase();

            Producto producto = productos.get(productoId);
            if (producto == null) {
                System.out.println("Product not found.");
                continue;
            }

            System.out.print("Enter quantity sold: ");
            int cantidad = Integer.parseInt(scanner.nextLine().trim());

            vendedorSeleccionado.agregarVenta(producto, cantidad);

            System.out.print("Add another sale? (y/n): ");
            agregarVentas = scanner.nextLine().trim().equalsIgnoreCase("y");
        }

        // Display final sales summary
        vendedorSeleccionado.mostrarResumen(formatoColombiano);
        scanner.close();
    }
}