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
            
            webApp = container "Web Application" "Provee la interfaz web para los usuarios de la billetera." "React" "Web Browser"
            mobileApp = container "Mobile App" "Provee acceso rapido a la billetera desde el celular." "Flutter" "Mobile App"
            
            pagoyaApi = container "PagoYa API Application" "Provee toda la logica de negocio a traves de una API REST." "Java and Spring Boot" {
                authComponent = component "Auth Context" "Manejo de seguridad, autenticacion y sesiones." "Spring Security"
                customerComponent = component "Customer Context" "Gestion de perfiles e informacion del cliente." "Spring MVC Controller & Service"
                accountComponent = component "Account Context" "Cuentas digitales y validacion de saldos." "Spring MVC Controller & Service"
                transferComponent = component "Transfer Context" "Procesamiento de transferencias entre cuentas." "Spring MVC Controller & Service"
                billingComponent = component "Billing Context" "Gestiona catalogo de proveedores, pagos de servicios y pagos recurrentes." "Spring MVC Controller & Service"
            }
            database = container "PagoYa Database" "Almacena datos de clientes, cuentas, pagos y catalogos." "PostgreSQL 16" "Database"
            
            webApp -> pagoyaApi "Consume los endpoints" "JSON/HTTPS"
            mobileApp -> pagoyaApi "Consume los endpoints" "JSON/HTTPS"
            
            authComponent -> database "Valida credenciales" "JDBC/JPA"
            customerComponent -> database "Lee y escribe perfiles" "JDBC/JPA"
            accountComponent -> database "Actualiza saldos" "JDBC/JPA"
            transferComponent -> database "Registra historico de transferencias" "JDBC/JPA"
            billingComponent -> database "Registra recibos pagados" "JDBC/JPA"
            
            transferComponent -> accountComponent "Valida fondos suficientes" "Llamada interna"
            billingComponent -> accountComponent "Debita saldo de la cuenta" "Llamada interna"
        }
        
        providerSystem = softwareSystem "Proveedor de Servicios" "Sistemas externos de las empresas de servicios (Agua, Luz, Internet)." "External System"
        
        customer -> webApp "Consulta saldo y hace transferencias en" "HTTPS"
        customer -> mobileApp "Paga servicios y revisa cuenta en" "App"
        pagoyaSystem -> providerSystem "Notifica cancelacion de deuda" "REST/HTTPS"
        billingComponent -> providerSystem "Consulta validez del recibo" "JSON/HTTPS"
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
