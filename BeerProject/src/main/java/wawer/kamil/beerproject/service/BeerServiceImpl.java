package wawer.kamil.beerproject.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import wawer.kamil.beerproject.domain.Beer;
import wawer.kamil.beerproject.domain.Brewery;
import wawer.kamil.beerproject.exceptions.NoContentException;
import wawer.kamil.beerproject.repositories.BeerRepository;
import wawer.kamil.beerproject.repositories.BreweryRepository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import static org.apache.commons.io.FileUtils.writeByteArrayToFile;


@Service
@AllArgsConstructor
@Slf4j(topic = "application.logger")
public class BeerServiceImpl implements BeerService {


    private final BeerRepository beerRepository;
    private final BreweryRepository breweryRepository;

    private static final String THE_BREWERY_BASE_ON_ID_HAS_NOT_BEEN_FOUND = "The brewery base on id: {} has not been found";
    private static final String THE_BEER_BASE_ON_ID_HAS_NOT_BEEN_FOUND = "The beer base on id: {} has not been found";

    //get beers

    @Override
    public Page<Beer> findAllBeersPage(Pageable pageable) {
        return beerRepository.findAll(pageable);
    }

    @Override
    public List<Beer> findAllBeersList() {
        return beerRepository.findAll();
    }

    @Override
    public Beer findBeerByBeerId(Long beerId) throws NoContentException {
        if (beerRepository.existsBeerByBeerId(beerId)) {
            return beerRepository.findBeerByBeerId(beerId);
        } else {
            log.debug(THE_BEER_BASE_ON_ID_HAS_NOT_BEEN_FOUND, beerId);
            throw new NoContentException();
        }
    }

    //get beers by breweryID

    @Override
    public Page<Beer> findAllBeersByBreweryIdPage(Long breweryId, Pageable pageable) throws NoContentException {
        if (breweryRepository.existsBreweryByBreweryId(breweryId)) {
            Brewery brewery = breweryRepository.findByBreweryId(breweryId);
            return beerRepository.findAllByBrewery(brewery, pageable);
        } else {
            log.debug(THE_BREWERY_BASE_ON_ID_HAS_NOT_BEEN_FOUND, breweryId);
            throw new NoContentException();
        }
    }

    @Override
    public List<Beer> findAllBeersByBreweryIdList(Long breweryId) throws NoContentException {
        if (breweryRepository.existsBreweryByBreweryId(breweryId)) {
            Brewery brewery = breweryRepository.findByBreweryId(breweryId);
            return beerRepository.findAllByBrewery(brewery);
        } else {
            log.debug(THE_BREWERY_BASE_ON_ID_HAS_NOT_BEEN_FOUND, breweryId);
            throw new NoContentException();
        }
    }

    @Override
    public Beer findProperBeerByBreweryIdAndBeerId(Long breweryId, Long beerId) throws NoContentException {
        if (breweryRepository.existsBreweryByBreweryId(breweryId)) {
            if (beerRepository.existsBeerByBeerId(beerId)) {
                Brewery brewery = breweryRepository.findByBreweryId(breweryId);
                return beerRepository.findBeerByBreweryAndBeerId(brewery, beerId);
            } else {
                log.debug(THE_BEER_BASE_ON_ID_HAS_NOT_BEEN_FOUND, beerId);
                throw new NoContentException();
            }
        } else {
            log.debug(THE_BREWERY_BASE_ON_ID_HAS_NOT_BEEN_FOUND, breweryId);
            throw new NoContentException();
        }
    }

    //post beers

    @Override
    public Beer addNewBeerToRepository(Beer beer) {
        return beerRepository.save(beer);
    }

    @Override
    public Beer addNewBeerAssignedToBreweryByBreweryId(Long breweryID, Beer beer) throws NoContentException {
        return breweryRepository.findById(breweryID)
                .map(brewery -> {
                    beer.setBrewery(brewery);
                    return beerRepository.save(beer);
                }).orElseThrow(NoContentException::new);
    }

    //put beers

    @Override
    public Beer updateBeerByBeerId(Long beerId, Beer beer) throws NoContentException {
        if (beerRepository.existsBeerByBeerId(beerId)) {
            beer.setBeerId(beerId);
            return beerRepository.save(beer);
        } else {
            log.debug(THE_BEER_BASE_ON_ID_HAS_NOT_BEEN_FOUND, beerId);
            throw new NoContentException();
        }
    }

    public Beer updateBeerByBreweryIdAndBeerId(Long breweryId, Long beerId, Beer updatedBeer) throws NoContentException {
        if (breweryRepository.existsBreweryByBreweryId(breweryId)) {
            if (beerRepository.existsBeerByBeerId(beerId)) {
                Beer beer = beerRepository.findBeerByBeerId(beerId);
                beer.setName(updatedBeer.getName());
                beer.setAlcohol(updatedBeer.getAlcohol());
                beer.setStyle(updatedBeer.getStyle());
                beer.setExtract(updatedBeer.getExtract());
                beer.setImage(updatedBeer.getImage());
                return beerRepository.save(beer);
            } else {
                log.debug(THE_BEER_BASE_ON_ID_HAS_NOT_BEEN_FOUND, beerId);
                throw new NoContentException();
            }
        } else {
            log.debug(THE_BREWERY_BASE_ON_ID_HAS_NOT_BEEN_FOUND, breweryId);
            throw new NoContentException();
        }
    }

    //delete beers

    @Override
    public void deleteBeerByBeerId(Long beerId) throws NoContentException {
        if (beerRepository.existsBeerByBeerId(beerId)) {
            beerRepository.deleteById(beerId);
        } else {
            log.debug(THE_BEER_BASE_ON_ID_HAS_NOT_BEEN_FOUND, beerId);
            throw new NoContentException();
        }
    }

    @Override
    public void deleteBeerByBreweryIdAndBeerId(Long breweryId, Long beerId) throws NoContentException {
        if (breweryRepository.existsBreweryByBreweryId(breweryId) && beerRepository.existsBeerByBeerId(beerId)) {
            beerRepository.deleteById(beerId);
        } else {
            log.debug("The brewery base on breweryId: {} or beer base on beerId: {} has not been found", breweryId, beerId);
            throw new NoContentException();
        }
    }

    @Override
    public void uploadBeerImageToImagesDirectory(MultipartFile file) throws IOException {
        String path = "src/main/resources/images/.";
        File createdFile = new File(path.substring(0, path.length()-1) + file.getOriginalFilename());
        try(FileOutputStream outputStream = new FileOutputStream(createdFile)) {
            outputStream.write(file.getBytes());
        } catch (FileNotFoundException e) {
            log.debug(e.getMessage());
        }

    }

    @Override
    @Transactional
    public void setBeerImageToProperBeerBaseOnBeerId(Long breweryId, Long beerId, MultipartFile file) throws IOException, NoContentException {
        Beer beer = beerRepository.findBeerByBeerId(beerId);
        byte[] byteObject = new byte[file.getBytes().length];
        int i = 0;
        for (byte b : file.getBytes()) {
            byteObject[i++] = b;
        }
        beer.setImage(byteObject);
        beerRepository.save(beer);
    }

    @Override
    public byte [] downloadImageFromDb(Long beerId) throws IOException {
        Beer beer = beerRepository.findBeerByBeerId(beerId);
        byte [] imageInBytes = beer.getImage();
        return imageInBytes;
    }
}
