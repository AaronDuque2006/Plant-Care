package ad.PlantTrack.service.impl;

import ad.PlantTrack.entity.Species;
import ad.PlantTrack.payload.request.SpeciesRequest;
import ad.PlantTrack.repository.SpeciesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpeciesService {

    @Autowired
    SpeciesRepository speciesRepository;

    public List<Species> getAllSpecies() {
        return speciesRepository.findAll();
    }

    public Species getSpeciesById(Long id) {
        return speciesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Especie no encontrada"));
    }

    public Species createSpecies(SpeciesRequest request) {
        Species species = new Species();
        species.setCommonName(request.getCommonName());
        species.setScientificName(request.getScientificName());
        species.setWateringFrequencyDays(request.getWateringFrequencyDays());
        species.setFertilizationFrequencyDays(request.getFertilizationFrequencyDays());
        species.setSunlightNeeds(request.getSunlightNeeds());
        species.setCareTips(request.getCareTips());

        return speciesRepository.save(species);
    }
}
