package lu.kbra.springtest.controller.api.private_;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lu.kbra.springtest.comp.perm.AnyPermission;
import lu.kbra.springtest.db.UserPermission;
import lu.kbra.springtest.db.data.ItemData;
import lu.kbra.springtest.db.table.ItemTable;

@RestController("privateItemController")
@RequestMapping("/api/private/item")
public class ItemController {

	public record CreateItemData(
			@NotBlank String name,
			@NotBlank String description,
			@DecimalMin(value = "0", inclusive = true) long price,
			boolean active) {

	}

	@Autowired
	private ItemTable itemTable;

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
		final ItemData data = new ItemData(itemData.name(), itemData.description(), itemData.price(), itemData.active());

		itemTable.insertAndReload(data);

		return ResponseEntity.created(URI.create("/api/public/item/get/id/" + data.getId())).body(data);
	}

}
