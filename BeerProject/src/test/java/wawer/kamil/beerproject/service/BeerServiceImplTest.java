package wawer.kamil.beerproject.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Pageable;
import wawer.kamil.beerproject.domain.Beer;
import wawer.kamil.beerproject.domain.Brewery;
import wawer.kamil.beerproject.exceptions.NoContentException;
import wawer.kamil.beerproject.repositories.BeerRepository;
import wawer.kamil.beerproject.repositories.BreweryRepository;

import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BeerServiceImplTest {

    @Mock
    Pageable pageable;

    @Mock
    Beer beer;

    @Mock
    Brewery brewery;

    @Mock
    BeerRepository beerRepository;

    @Mock
    BreweryRepository breweryRepository;

    @InjectMocks
    BeerServiceImpl service;

    private static final Long beerID = 1L;
    private static final Long breweryID = 1L;

    @Test
    public void verify_find_all_beers_page() {
        service.findAllBeersPage(pageable);
        verify(beerRepository).findAll(pageable);
    }

    @Test
    public void verify_find_all_beers_list(){
        service.findAllBeersList();
        verify(beerRepository).findAll();
    }

    @Test
    public void verify_find_beer_by_beer_id_when_id_exists() throws NoContentException {
        when(beerRepository.existsBeerByBeerId(beerID)).thenReturn(true);
        service.findBeerByBeerId(beerID);
        verify(beerRepository).findBeerByBeerId(beerID);
    }

    @Test(expected = NoContentException.class)
    public void verify_find_beer_by_beer_id_when_id_do_not_exists() throws NoContentException {
        when(beerRepository.existsBeerByBeerId(beerID)).thenReturn(false);
        service.findBeerByBeerId(beerID);
        verify(beerRepository).findBeerByBeerId(beerID);
    }

    @Test
    public void verify_find_all_beers_by_brewery_id_when_id_exists() throws NoContentException {
        when(breweryRepository.existsBreweryByBreweryId(breweryID)).thenReturn(true);
        when(breweryRepository.findByBreweryId(breweryID)).thenReturn(brewery);
        service.findAllBeersByBreweryIdPage(breweryID, pageable);
        verify(beerRepository).findAllByBrewery(brewery, pageable);
    }

    @Test(expected = NoContentException.class)
    public void verify_find_all_beers_by_brewery_id_when_id_do_not_exists() throws NoContentException {
        when(breweryRepository.existsBreweryByBreweryId(breweryID)).thenReturn(false);
        service.findAllBeersByBreweryIdPage(breweryID, pageable);
    }

    @Test
    public void verify_find_all_beers_by_brewery_id_list_when_id_exists() throws NoContentException {
        when(breweryRepository.existsBreweryByBreweryId(breweryID)).thenReturn(true);
        when(breweryRepository.findByBreweryId(breweryID)).thenReturn(brewery);
        service.findAllBeersByBreweryIdList(breweryID);
        verify(beerRepository).findAllByBrewery(brewery);
    }

    @Test(expected = NoContentException.class)
    public void verify_find_all_beers_by_brewery_id_list_when_id_do_not_exists() throws NoContentException {
        when(breweryRepository.existsBreweryByBreweryId(breweryID)).thenReturn(false);
        service.findAllBeersByBreweryIdList(breweryID);
    }

    @Test
    public void verify_find_proper_beer_by_brewery_id_and_beerId_when_both_ids_exist() throws NoContentException {
        when(breweryRepository.existsBreweryByBreweryId(breweryID)).thenReturn(true);
        when(beerRepository.existsBeerByBeerId(beerID)).thenReturn(true);
        when(breweryRepository.findByBreweryId(breweryID)).thenReturn(brewery);
        service.findProperBeerByBreweryIdAndBeerId(breweryID, beerID);
        verify(beerRepository).findBeerByBreweryAndBeerId(brewery, beerID);
    }

    @Test(expected = NoContentException.class)
    public void verify_find_proper_beer_by_brewery_id_and_beerId_when_only_brewery_id_exist() throws NoContentException {
        when(breweryRepository.existsBreweryByBreweryId(breweryID)).thenReturn(false);
        service.findProperBeerByBreweryIdAndBeerId(breweryID, beerID);
    }

    @Test(expected = NoContentException.class)
    public void verify_find_proper_beer_by_brewery_id_and_beerId_when_only_beer_id_exist() throws NoContentException {
        when(breweryRepository.existsBreweryByBreweryId(breweryID)).thenReturn(true);
        when(beerRepository.existsBeerByBeerId(beerID)).thenReturn(false);
        service.findProperBeerByBreweryIdAndBeerId(breweryID, beerID);
    }

    @Test
    public void verify_add_new_beer_to_repository() {
        service.addNewBeerToRepository(beer);
        verify(beerRepository).save(beer);
    }

    @Test
    public void verify_add_new_beer_assigned_to_brewery_by_brewery_id_when_id_exists() throws NoContentException {
        when(breweryRepository.findById(breweryID)).thenReturn(Optional.of(brewery));
        when(beerRepository.save(beer)).thenReturn(beer);
        service.addNewBeerAssignedToBreweryByBreweryId(breweryID, beer);
        verify(breweryRepository).findById(beerID);
    }

    @Test(expected = NoContentException.class)
    public void verify_add_new_beer_assigned_to_brewery_by_brewery_id_when_id_do_not_exists() throws NoContentException {
        when(breweryRepository.findById(breweryID)).thenReturn(Optional.empty());
        service.addNewBeerAssignedToBreweryByBreweryId(breweryID, beer);
    }

    //update

    @Test
    public void verify_update_beer_by_beer_id_when_id_exists() throws NoContentException {
        when(beerRepository.existsBeerByBeerId(beerID)).thenReturn(true);
        service.updateBeerByBeerId(breweryID, beer);
        verify(beerRepository).save(beer);
    }

    @Test(expected = NoContentException.class)
    public void verify_update_beer_by_beer_id_when_id_do_not_exists() throws NoContentException {
        when(beerRepository.existsBeerByBeerId(beerID)).thenReturn(false);
        service.updateBeerByBeerId(breweryID, beer);
    }

    @Test
    public void verify_update_beer_by_brewery_id_and_beer_id_when_both_ids_exist() throws NoContentException {
        when(breweryRepository.existsBreweryByBreweryId(breweryID)).thenReturn(true);
        when(beerRepository.existsBeerByBeerId(beerID)).thenReturn(true);
        when(beerRepository.findBeerByBeerId(beerID)).thenReturn(beer);
        service.updateBeerByBreweryIdAndBeerId(breweryID, beerID, beer);
        verify(beerRepository).save(beer);
    }

    @Test(expected = NoContentException.class)
    public void verify_update_beer_by_brewery_id_and_beer_id_when_brewery_id_do_not_exist() throws NoContentException {
        when(breweryRepository.existsBreweryByBreweryId(breweryID)).thenReturn(false);
        service.updateBeerByBreweryIdAndBeerId(breweryID, beerID, beer);
    }

    @Test(expected = NoContentException.class)
    public void verify_update_beer_by_brewery_id_and_beer_id_when_only_brewery_id_exist() throws NoContentException {
        when(breweryRepository.existsBreweryByBreweryId(breweryID)).thenReturn(true);
        when(beerRepository.existsBeerByBeerId(breweryID)).thenReturn(false);
        service.updateBeerByBreweryIdAndBeerId(breweryID, beerID, beer);
    }

    //delete

    @Test
    public void verify_delete_beer_by_beer_id_when_id_exists() throws NoContentException {
        when(beerRepository.existsBeerByBeerId(beerID)).thenReturn(true);
        service.deleteBeerByBeerId(beerID);
        verify(beerRepository).deleteById(beerID);
    }

    @Test(expected = NoContentException.class)
    public void verify_delete_beer_by_beer_id_when_id_do_not_exists() throws NoContentException {
        when(beerRepository.existsBeerByBeerId(beerID)).thenReturn(false);
        service.deleteBeerByBeerId(beerID);
    }

    @Test
    public void verify_delete_beer_by_brewery_id_and_beer_id_when_both_ids_exist() throws NoContentException {
        when(breweryRepository.existsBreweryByBreweryId(breweryID)).thenReturn(true);
        when(beerRepository.existsBeerByBeerId(beerID)).thenReturn(true);
        service.deleteBeerByBreweryIdAndBeerId(breweryID, beerID);
        verify(beerRepository).deleteById(beerID);
    }

    @Test(expected = NoContentException.class)
    public void verify_delete_beer_by_brewery_id_and_beer_id_when_brewery_id_do_not_exists() throws NoContentException {
        when(breweryRepository.existsBreweryByBreweryId(breweryID)).thenReturn(false);
        service.deleteBeerByBreweryIdAndBeerId(breweryID, beerID);
    }

    @Test(expected = NoContentException.class)
    public void verify_delete_beer_by_brewery_id_and_beer_id_when_beer_id_do_not_exists() throws NoContentException {
        when(breweryRepository.existsBreweryByBreweryId(breweryID)).thenReturn(true);
        when(beerRepository.existsBeerByBeerId(beerID)).thenReturn(false);
        service.deleteBeerByBreweryIdAndBeerId(breweryID, beerID);
    }
}
