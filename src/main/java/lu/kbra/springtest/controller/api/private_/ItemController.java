package lu.kbra.springtest.controller.api.private_;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lu.kbra.springtest.comp.perm.AnyPermission;
import lu.kbra.springtest.db.data.ItemData;
import lu.kbra.springtest.db.data.UserPermission;
import lu.kbra.springtest.db.table.ItemTable;
import lu.kbra.springtest.utils.Ean13Generator;

@RestController
@RequestMapping("/api/private/item")
public class ItemController {

	public record CreateItemData(
			@NotBlank String name,
			@NotBlank String description,
			@DecimalMin(value = "0", inclusive = true) long price) {

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

	@AnyPermission(UserPermission.ITEM_CREATE)
	@PutMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ItemData> createItemPut(@Valid @RequestBody final CreateItemData itemData) {
		return createItem_(itemData);
	}

	@AnyPermission(UserPermission.ITEM_CREATE)
	@PutMapping(value = "/create", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public ResponseEntity<ItemData> createItemForm(@Valid @ModelAttribute final CreateItemData itemData) {
		return createItem_(itemData);
	}

	private ResponseEntity<ItemData> createItem_(CreateItemData itemData) {
		String ean = null;
		do {
			ean = Ean13Generator.generate();
		} while (this.itemTable.byCode(ean).isPresent());

		final char[] eanChars = new char[13];
		ean.getChars(0, 13, eanChars, 0);
		final ItemData data = new ItemData(eanChars, itemData.name(), itemData.description(), itemData.price());

		itemTable.insertAndReload(data);

		return ResponseEntity.created(URI.create("/api/private/item/get/id/" + data.getId())).body(data);
	}

}
