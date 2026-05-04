/*
 * Grupo 2 - Ing Software
 * Integrantes:
 * - u202419995 Aguilar Anticona, Piero Antonio
 * - u20191a851 Bustamante Cruzado, Raúl Tomás
 * - u202311021 Saavedra Cervera, Sergio Andres
 * - u202210236 Chipoco Mejia, Jorge Piero Jesus
 */
workspace "PagoYa API" "Plataforma Fintech de Pagos y Billetera Digital" {
    model {
        customer = person "Cliente PagoYa" "Un cliente de la plataforma que realiza pagos."
        
        pagoyaSystem = softwareSystem "PagoYa System" "Gestiona autenticacion, clientes, cuentas, transferencias y pago de servicios." {
            pagoyaApi = container "PagoYa API Application" "Provee toda la logica de negocio a traves de una API REST." "Java and Spring Boot" {
                billingComponent = component "Billing Context" "Gestiona catalogo de proveedores, pagos de servicios y pagos recurrentes." "Spring Component"
                authComponent = component "Auth Context" "Manejo de autenticacion y sesiones." "Spring Component"
                customerComponent = component "Customer Context" "Gestion de perfiles de cliente." "Spring Component"
                accountComponent = component "Account Context" "Cuentas digitales y saldos." "Spring Component"
                transferComponent = component "Transfer Context" "Transferencias entre cuentas." "Spring Component"
            }
            database = container "PagoYa Database" "Almacena datos de clientes, cuentas, pagos y catalogos." "PostgreSQL 16" "Database"
            
            pagoyaApi -> database "Lee y escribe datos en" "JDBC/JPA"
        }
        
        providerSystem = softwareSystem "Proveedor de Servicios" "Sistemas externos de las empresas de servicios (Agua, Luz, Internet)." "External System"
        
        customer -> pagoyaSystem "Consulta saldo y paga servicios a traves de" "REST/HTTPS"
        pagoyaSystem -> providerSystem "Notifica la cancelacion del recibo (integracion futura)" "REST/HTTPS"
    }
    
    views {
        systemContext pagoyaSystem "SystemContext" {
            include *
            autoLayout
        }
        
        container pagoyaSystem "Containers" {
            include *
            autoLayout
        }
        
        component pagoyaApi "Components" {
            include *
            autoLayout
        }
        
        theme default
    }
}
