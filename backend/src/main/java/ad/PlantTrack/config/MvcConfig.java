package ad.PlantTrack.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Expose the "uploads" directory to be accessible via HTTP
        Path uploadDir = Paths.get("uploads");
        String uploadPath = uploadDir.toFile().getAbsolutePath();
        // Ensure directory exists
        if (!uploadDir.toFile().exists()) {
            uploadDir.toFile().mkdirs();
        }

        System.out.println("DEBUG: MvcConfig serving resources from: " + uploadPath);

        // Use the absolute file path with the "file:" prefix so Spring serves files
        registry.addResourceHandler("/uploads/**")
            .addResourceLocations("file:" + uploadPath + "/");
    }
}
