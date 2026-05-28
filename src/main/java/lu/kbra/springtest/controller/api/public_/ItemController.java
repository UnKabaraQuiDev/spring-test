package lu.kbra.springtest.controller.api.public_;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lu.kbra.springtest.db.data.ItemData;
import lu.kbra.springtest.db.table.ItemTable;

@RestController("publicItemController")
@RequestMapping("/api/public/item")
public class ItemController {

	public record CreateItemData(
			@NotBlank String name,
			@NotBlank String description,
			@DecimalMin(value = "0", inclusive = true) long price,
			boolean active) {

	}

	@Autowired
	private ItemTable itemTable;

	@GetMapping("/get/id/{id}")
	public Optional<ItemData> getItem(@PathVariable int id) {
		return itemTable.byId(id);
	}

	@GetMapping("/get/ids/{from}..{to}")
	public List<Optional<ItemData>> getItems(@PathVariable int from, @PathVariable int to) {
		return IntStream.rangeClosed(from, to).mapToObj(itemTable::byId).filter(Optional::isPresent).collect(Collectors.toList());
	}

	@GetMapping("/get/active/{state}")
	public List<ItemData> getActiveItems(@PathVariable boolean state) {
		return itemTable.byActive(state);
	}

}
